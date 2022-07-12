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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SagaTransactor {

  private static final Logger LOG = LogManager.getLogger(SagaTransactor.class);

  public <A> A transact(String sagaName, Saga<A> saga) {
    return run(sagaName, saga, new Stack<>()).getValue();
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
      Callable<X> action = sagaStep.getAction().getAction();
      compensations.add(sagaStep.getCompensator());
      long actionStart = System.currentTimeMillis();
      try {
        X callResult = Failsafe.with(sagaStep.getAction().getRetryPolicy()).get(action::call);
        LOG.info("SAGA:{} [action] {} {}(ms)", sagaName, sagaStep.getAction().getName(), System.currentTimeMillis() - actionStart);
        return EvaluationResult.success(callResult);
      } catch (Throwable ta) {
        LOG.info("SAGA:{} [action] {} {}(ms)", sagaName, sagaStep.getAction().getName(), System.currentTimeMillis() - actionStart);
        long compensationStart = System.currentTimeMillis();
        String compensation = null;
        try {
          while (!compensations.empty()) {
            SagaCompensation pop = compensations.pop();
            compensationStart = System.currentTimeMillis();
            compensation = pop.getName();
            Failsafe.with(pop.getRetryPolicy()).run(() -> pop.getCompensation().run());
            LOG.info("SAGA:{} [compensation] {} {}(ms)", sagaName, sagaStep.getAction().getName(), System.currentTimeMillis() - compensationStart);
          }
        } catch (Throwable tc) {
          LOG.info("SAGA:{} [compensation] {} {}(ms)", sagaName, sagaStep.getAction().getName(), System.currentTimeMillis() - compensationStart);
          return EvaluationResult.failed(new SagaCompensationFailedException(compensation, sagaName, tc));
        }
        return EvaluationResult.failed(new SagaActionFailedException(sagaStep.getAction().getName(), sagaName, ta));
      }
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
}
