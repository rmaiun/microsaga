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

public class SagaTransactor {


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
    EvaluationResult<A> result = run(sagaName, saga, new Stack<>());
    if (result.isError()) {
      Throwable error = result.getError();
      throw errorTransformer.apply(error);
    }
    return result.getValue();
  }

  public <X, Y> EvaluationResult<X> run(String sagaName, Saga<X> saga, Stack<SagaCompensation> compensations) {
    if (saga instanceof SagaSuccess) {
      return EvaluationResult.success(((SagaSuccess<X>) saga).getValue());
    } else if (saga instanceof SagaStep) {
      SagaStep<X> sagaStep = (SagaStep<X>) saga;
      Callable<X> action = sagaStep.getAction().getAction();
      System.out.printf("Evaluating action ---> %s%n", sagaStep.getAction().getName());
      compensations.add(sagaStep.getCompensator());
      try {
        return EvaluationResult.success(action.call());
      } catch (Throwable ta) {
        try {
          while (!compensations.empty()) {
            SagaCompensation pop = compensations.pop();
            System.out.printf("Evaluating compensation <--- %s%n", pop.getName());
            pop.getCompensation().run();
          }
        } catch (Throwable tc) {
          return EvaluationResult.failed(new SagaCompensationFailedException(sagaStep.getAction().getName(), sagaName, tc));
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
      return EvaluationResult.failed(new IllegalArgumentException("Could not define Saga Operation"));
    }
  }

}
