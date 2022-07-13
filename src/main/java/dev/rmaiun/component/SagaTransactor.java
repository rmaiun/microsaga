package dev.rmaiun.component;

import dev.rmaiun.exception.SagaActionFailedException;
import dev.rmaiun.exception.SagaCompensationFailedException;
import dev.rmaiun.saga.Saga;
import dev.rmaiun.saga.SagaFlatMap;
import dev.rmaiun.saga.SagaStep;
import dev.rmaiun.saga.SagaSuccess;
import dev.rmaiun.support.EvaluationResult;
import dev.rmaiun.support.SagaCompensation;
import java.util.Stack;
import java.util.concurrent.Callable;
import java.util.function.Function;
import net.jodah.failsafe.Failsafe;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;

public class SagaTransactor {

  private final Level loggingLvl;

  public <A> A transact(String sagaName, Saga<A> saga) {
    return run(sagaName, saga, new Stack<>()).getValue();
  }

  public SagaTransactor(Level loggingLvl) {
    if (loggingLvl == null) {
      this.loggingLvl = Level.INFO;
    } else {
      this.loggingLvl = loggingLvl;
    }
  }

  public <A> A transactOrThrow(String sagaName, Saga<A> saga) {
    EvaluationResult<A> result = run(sagaName, saga, new Stack<>());
    if (result.isError()) {
      throw result.getError();
    }
    return result.getValue();
  }

  public <A, E extends RuntimeException> A transactOrThrow(String sagaName, Saga<A> saga, Function<Throwable, E> errorTransformer) {
    EvaluationResult<A> result = runLogged(sagaName, saga);
    if (result.isError()) {
      Throwable error = result.getError();
      throw errorTransformer.apply(error);
    }
    return result.getValue();
  }

  private <X> EvaluationResult<X> runLogged(String sagaName, Saga<X> saga) {
    return run(sagaName, saga, new Stack<>());
  }

  public <X, Y> EvaluationResult<X> run(String sagaName, Saga<X> saga, Stack<SagaCompensation> compensations) {
    if (saga instanceof SagaSuccess) {
      return EvaluationResult.success(((SagaSuccess<X>) saga).getValue());
    } else if (saga instanceof SagaStep) {
      SagaStep<X> sagaStep = (SagaStep<X>) saga;
      return evaluateStep(sagaName, sagaStep, compensations);
    } else if (saga instanceof SagaFlatMap) {
      SagaFlatMap<Y, X> sagaFlatMap = (SagaFlatMap<Y, X>) saga;
      EvaluationResult<Y> runA = run(sagaName, sagaFlatMap.getA(), compensations);
      return runA.isSuccess()
          ? run(sagaName, sagaFlatMap.getfB().apply(runA.getValue()), compensations)
          : EvaluationResult.failed(runA.getError());
    } else {
      return EvaluationResult.failed(new IllegalArgumentException("Invalid Saga Operation"));
    }
  }

  private <X> EvaluationResult<X> evaluateStep(String sagaName, SagaStep<X> sagaStep, Stack<SagaCompensation> compensations) {
    Callable<X> action = sagaStep.getAction().getAction();
    compensations.add(sagaStep.getCompensator());
    long actionStart = System.currentTimeMillis();
    try {
      X callResult = Failsafe.with(sagaStep.getAction().getRetryPolicy()).get(action::call);
      logAction(sagaName, sagaStep.getAction().getName(), System.currentTimeMillis() - actionStart);
      return EvaluationResult.success(callResult);
    } catch (Throwable ta) {
      logAction(sagaName, sagaStep.getAction().getName(), System.currentTimeMillis() - actionStart);
      long compensationStart = System.currentTimeMillis();
      String compensation = null;
      try {
        while (!compensations.empty()) {
          SagaCompensation pop = compensations.pop();
          compensationStart = System.currentTimeMillis();
          compensation = pop.getName();
          Failsafe.with(pop.getRetryPolicy()).run(() -> pop.getCompensation().run());
          logCompensation(sagaName, compensation, System.currentTimeMillis() - compensationStart);
        }
      } catch (Throwable tc) {
        logCompensation(sagaName, compensation, System.currentTimeMillis() - compensationStart);
        return EvaluationResult.failed(new SagaCompensationFailedException(compensation, sagaName, tc));
      }
      return EvaluationResult.failed(new SagaActionFailedException(sagaStep.getAction().getName(), sagaName, ta));
    }
  }

  private void logCompensation(String sagaName, String compensationName, long msDiff) {
    log(sagaName, "compensation", compensationName, msDiff);
  }

  private void logAction(String sagaName, String compensationName, long msDiff) {
    log(sagaName, "action", compensationName, msDiff);
  }

  private void log(String sagaName, String type, String compensationName, long msDiff) {
    LogManager.getContext().getLogger(SagaTransactor.class).log(loggingLvl, "SAGA:{} [{}] {} {}(ms)", sagaName, type, compensationName, msDiff);
  }
}
