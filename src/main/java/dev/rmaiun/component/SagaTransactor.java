package dev.rmaiun.component;

import dev.rmaiun.saga.Saga;
import dev.rmaiun.saga.SagaFailed;
import dev.rmaiun.saga.SagaFlatMap;
import dev.rmaiun.saga.SagaStep;
import dev.rmaiun.saga.SagaSuccess;
import dev.rmaiun.support.EvaluationResult;
import dev.rmaiun.support.SagaCompensation;
import java.util.Stack;
import java.util.concurrent.Callable;
import java.util.function.Function;

public class SagaTransactor {


  public <A> A transact(Saga<A> saga) {
    return run(saga, new Stack<>()).getValue();
  }

  public <A> A transactOrThrow(Saga<A> saga) {
    EvaluationResult<A> result = run(saga, new Stack<>());
    if (result.isError()) {
      throw new RuntimeException(result.getError());
    }
    return result.getValue();
  }

  public <A, E extends RuntimeException> A transactOrThrow(Saga<A> saga, Function<Throwable, E> errorTransformer) {
    EvaluationResult<A> result = run(saga, new Stack<>());
    if (result.isError()) {
      Throwable error = result.getError();
      throw errorTransformer.apply(error);
    }
    return result.getValue();
  }

  public <X, Y> EvaluationResult<X> run(Saga<X> saga, Stack<SagaCompensation> compensations) {
    if (saga instanceof SagaSuccess) {
      return EvaluationResult.success(((SagaSuccess<X>) saga).getValue());
    } else if (saga instanceof SagaStep) {
      SagaStep<X> sagaStep = (SagaStep<X>) saga;
      Callable<X> action = sagaStep.getAction().getAction();
      System.out.printf("Evaluating action ---> %s%n \n", sagaStep.getAction().getName());
      compensations.add(sagaStep.getCompensator());
      try {
        return EvaluationResult.success(action.call());
      } catch (Throwable t) {
        while (!compensations.empty()) {
          SagaCompensation pop = compensations.pop();
          System.out.printf("Evaluating compensation <--- %s \n", pop.getName());
          pop.getCompensation().run();
        }
        return EvaluationResult.actionFailed(t);
      }
    } else if (saga instanceof SagaFailed) {
      SagaFailed<X> sagaFailed = (SagaFailed<X>) saga;
      return EvaluationResult.actionFailed(sagaFailed.getCause());
    } else if (saga instanceof SagaFlatMap) {
      SagaFlatMap<Y, X> sagaFlatMap = (SagaFlatMap<Y, X>) saga;
      EvaluationResult<Y> runA = run(sagaFlatMap.getA(), compensations);
      return runA.isSuccess()
          ? run(sagaFlatMap.getfB().apply(runA.getValue()), compensations)
          : null;
    } else {
      return null;
    }
  }

}
