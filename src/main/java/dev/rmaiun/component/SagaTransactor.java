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

public class SagaTransactor {

  public <A> A transact(String sagaName, Saga<A> saga) {
    return runLogged(sagaName, saga).getValue();
  }

  public <A> A transactOrThrow(String sagaName, Saga<A> saga) {
    EvaluationResult<A> result = runLogged(sagaName, saga);
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
    StringBuilder sb = new StringBuilder();
    EvaluationResult<X> result = run(sagaName, saga, new Stack<>(), sb);
    System.out.println(sb);
    return result;
  }

  public <X, Y> EvaluationResult<X> run(String sagaName, Saga<X> saga, Stack<SagaCompensation> compensations, StringBuilder sb) {
    if (saga instanceof SagaSuccess) {
      return EvaluationResult.success(((SagaSuccess<X>) saga).getValue());
    } else if (saga instanceof SagaStep) {
      SagaStep<X> sagaStep = (SagaStep<X>) saga;
      Callable<X> action = sagaStep.getAction().getAction();
      compensations.add(sagaStep.getCompensator());
      try {
        long actionStart = System.currentTimeMillis();
        X callResult = action.call();
        long actionEnd = System.currentTimeMillis();
        sb.append(String.format("Evaluating action ---> %s (%d ms) %n", sagaStep.getAction().getName(), actionEnd - actionStart));
        return EvaluationResult.success(callResult);
      } catch (Throwable ta) {
        try {
          while (!compensations.empty()) {
            SagaCompensation pop = compensations.pop();
            long compensationStart = System.currentTimeMillis();
            Failsafe.with(pop.getRetryPolicy()).run(() -> pop.getCompensation().run());
            long compensationEnd = System.currentTimeMillis();
            sb.append(String.format("Evaluating compensation ---> %s (%d ms) %n", sagaStep.getAction().getName(), compensationEnd - compensationStart));
          }
        } catch (Throwable tc) {
          return EvaluationResult.failed(new SagaCompensationFailedException(sagaStep.getAction().getName(), sagaName, tc));
        }
        return EvaluationResult.failed(new SagaActionFailedException(sagaStep.getAction().getName(), sagaName, ta));
      }
    } else if (saga instanceof SagaFlatMap) {
      SagaFlatMap<Y, X> sagaFlatMap = (SagaFlatMap<Y, X>) saga;
      EvaluationResult<Y> runA = run(sagaName, sagaFlatMap.getA(), compensations, sb);
      return runA.isSuccess()
          ? run(sagaName, sagaFlatMap.getfB().apply(runA.getValue()), compensations, sb)
          : EvaluationResult.failed(runA.getError());
    } else {
      return EvaluationResult.failed(new IllegalArgumentException("Invalid Saga Operation"));
    }
  }
}
