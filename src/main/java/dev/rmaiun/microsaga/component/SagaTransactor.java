package dev.rmaiun.microsaga.component;

import dev.rmaiun.microsaga.exception.SagaActionFailedException;
import dev.rmaiun.microsaga.exception.SagaCompensationFailedException;
import dev.rmaiun.microsaga.func.StubInputFunction;
import dev.rmaiun.microsaga.saga.Saga;
import dev.rmaiun.microsaga.saga.SagaFlatMap;
import dev.rmaiun.microsaga.saga.SagaStep;
import dev.rmaiun.microsaga.saga.SagaSuccess;
import dev.rmaiun.microsaga.support.EvaluationResult;
import dev.rmaiun.microsaga.support.SagaCompensation;
import java.util.Stack;
import java.util.concurrent.Callable;
import java.util.function.Function;
import net.jodah.failsafe.Failsafe;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;

public class SagaTransactor {

  private final Level loggingLvl;

  public <A> EvaluationResult<A> transact(String sagaName, Saga<A> saga) {
    return run(sagaName, saga);
  }

  public SagaTransactor(Level loggingLvl) {
    if (loggingLvl == null) {
      this.loggingLvl = Level.INFO;
    } else {
      this.loggingLvl = loggingLvl;
    }
  }

  public <A> A transactOrThrow(String sagaName, Saga<A> saga) {
    EvaluationResult<A> result = run(sagaName, saga);
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
    return run(sagaName, saga);
  }


  @SuppressWarnings("unchecked")
  public <X> EvaluationResult<X> run(String sagaName, Saga<X> sagaInput) {
    Stack<SagaCompensation> compensations = new Stack<>();
    Stack<Function<Object, Saga<Object>>> functions = new Stack<>();
    Function<Object, Saga<Object>> sagaInputFunc = (StubInputFunction<Saga<Object>>) o -> (Saga<Object>) sagaInput;
    functions.add(sagaInputFunc);
    EvaluationResult<Object> current = EvaluationResult.failed(new IllegalArgumentException("Empty saga defined"));
    while (!functions.empty()) {
      Function<Object, Saga<Object>> f = functions.pop();
      Saga<Object> saga = f instanceof StubInputFunction
          ? f.apply(null)
          : f.apply(current.getValue());
      if (saga instanceof SagaSuccess) {
        current = EvaluationResult.success(((SagaSuccess<X>) saga).getValue());
      } else if (saga instanceof SagaStep) {
        SagaStep<Object> sagaStep = (SagaStep<Object>) saga;
        current = evaluateStep(sagaName, sagaStep, compensations);
      } else if (saga instanceof SagaFlatMap) {
        SagaFlatMap<Object, Object> sagaFlatMap = (SagaFlatMap<Object, Object>) saga;
        functions.add(sagaFlatMap.getfB());
        functions.add(sagaFlatMap.getA());
      } else {
        current = EvaluationResult.failed(new IllegalArgumentException("Invalid Saga Operation"));
      }
    }
    return (EvaluationResult<X>) current;
  }

  private EvaluationResult<Object> evaluateStep(String sagaName, SagaStep<Object> sagaStep, Stack<SagaCompensation> compensations) {
    Callable<Object> action = sagaStep.getAction().getAction();
    compensations.add(sagaStep.getCompensator());
    long actionStart = System.currentTimeMillis();
    try {
      Object callResult = Failsafe.with(sagaStep.getAction().getRetryPolicy()).get(action::call);
      logAction(sagaName, sagaStep.getAction().getName(), System.currentTimeMillis() - actionStart);
      return EvaluationResult.success(callResult);
    } catch (Throwable ta) {
      logAction(sagaName, sagaStep.getAction().getName(), System.currentTimeMillis() - actionStart);
      long compensationStart = System.currentTimeMillis();
      String compensation = null;
      try {
        while (!compensations.empty()) {
          SagaCompensation pop = compensations.pop();
          if (!pop.isTechnical()) {
            compensationStart = System.currentTimeMillis();
            compensation = pop.getName();
            Failsafe.with(pop.getRetryPolicy()).run(() -> pop.getCompensation().run());
            logCompensation(sagaName, compensation, System.currentTimeMillis() - compensationStart);
          }
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
