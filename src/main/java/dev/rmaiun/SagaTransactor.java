package dev.rmaiun;

import java.util.Stack;
import java.util.concurrent.Callable;

public class SagaTransactor {


  public <A> A transact(Saga<A> saga) {
    return run(saga, new Stack<>());
  }

  public <X, Y> X run(Saga<X> saga, Stack<Runnable> compensations) {
    if (saga instanceof SagaSuccess) {
      return ((SagaSuccess<X>) saga).getValue();
    } else if (saga instanceof SagaStep) {
      SagaStep<X> sagaStep = (SagaStep<X>) saga;
      Callable<X> action = sagaStep.getAction();
      compensations.add(sagaStep.getCompensator());
      try {
        return action.call();
      } catch (Throwable t) {
        while (!compensations.empty()) {
          compensations.pop().run();
        }
        return null;
      }
    } else if (saga instanceof SagaFlatMap) {
      SagaFlatMap<Y, X> sagaFlatMap = (SagaFlatMap<Y, X>) saga;
      Y runA = run(sagaFlatMap.getA(), compensations);
      return run(runA == null ? null : sagaFlatMap.getfB().apply(runA), compensations);
    } else {
      return null;
    }
  }

}
