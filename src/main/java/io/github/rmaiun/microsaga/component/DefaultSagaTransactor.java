package io.github.rmaiun.microsaga.component;

import io.github.rmaiun.microsaga.exception.SagaActionFailedException;
import io.github.rmaiun.microsaga.exception.SagaCompensationFailedException;
import io.github.rmaiun.microsaga.func.StubInputFunction;
import io.github.rmaiun.microsaga.saga.Saga;
import io.github.rmaiun.microsaga.saga.SagaFlatMap;
import io.github.rmaiun.microsaga.saga.SagaStep;
import io.github.rmaiun.microsaga.saga.SagaSuccess;
import io.github.rmaiun.microsaga.support.Evaluation;
import io.github.rmaiun.microsaga.support.EvaluationHistory;
import io.github.rmaiun.microsaga.support.EvaluationResult;
import io.github.rmaiun.microsaga.support.SagaCompensation;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.Callable;
import java.util.function.Function;
import net.jodah.failsafe.Failsafe;

public class DefaultSagaTransactor implements SagaTransactor {

  @Override
  public <A> EvaluationResult<A> transact(String sagaName, Saga<A> saga) {
    return run(sagaName, saga);
  }

  @Override
  public <A> A transactOrThrow(String sagaName, Saga<A> saga) {
    EvaluationResult<A> result = run(sagaName, saga);
    if (result.isError()) {
      throw result.getError();
    }
    return result.getValue();
  }

  @Override
  public <A, E extends RuntimeException> A transactOrThrow(String sagaName, Saga<A> saga, Function<Throwable, E> errorTransformer) {
    EvaluationResult<A> result = run(sagaName, saga);
    if (result.isError()) {
      Throwable error = result.getError();
      throw errorTransformer.apply(error);
    }
    return result.getValue();
  }


  @SuppressWarnings("unchecked")
  public <X> EvaluationResult<X> run(String sagaName, Saga<X> sagaInput) {
    Stack<SagaCompensation> compensations = new Stack<>();
    Stack<Function<Object, Saga<Object>>> functions = new Stack<>();
    List<Evaluation> evaluations = new ArrayList<>();
    Function<Object, Saga<Object>> sagaInputFunc = (StubInputFunction<Saga<Object>>) o -> (Saga<Object>) sagaInput;
    functions.add(sagaInputFunc);
    EvaluationResult<Object> current = EvaluationResult.failed(new IllegalArgumentException("Empty saga defined"));
    boolean noError = true;
    while (!functions.empty() && noError) {
      Function<Object, Saga<Object>> f = functions.pop();
      Saga<Object> saga = f instanceof StubInputFunction
          ? f.apply(null)
          : f.apply(current.getValue());
      if (saga instanceof SagaSuccess) {
        current = EvaluationResult.success(((SagaSuccess<X>) saga).getValue());
        noError = !current.isError();
      } else if (saga instanceof SagaStep) {
        SagaStep<Object> sagaStep = (SagaStep<Object>) saga;
        current = evaluateStep(sagaName, sagaStep, compensations, evaluations);
        noError = !current.isError();
      } else if (saga instanceof SagaFlatMap) {
        SagaFlatMap<Object, Object> sagaFlatMap = (SagaFlatMap<Object, Object>) saga;
        functions.add(sagaFlatMap.getfB());
        functions.add(sagaFlatMap.getA());
      } else {
        current = EvaluationResult.failed(new IllegalArgumentException("Invalid Saga Operation"));
        noError = !current.isError();
      }
    }
    return new EvaluationResult<>((X) current.getValue(), new EvaluationHistory(sagaName, evaluations), current.getError());
  }

  private EvaluationResult<Object> evaluateStep(String sagaName, SagaStep<Object> sagaStep, Stack<SagaCompensation> compensations, List<Evaluation> evaluations) {
    Callable<Object> action = sagaStep.getAction().getAction();
    compensations.add(sagaStep.getCompensator());
    long actionStart = System.currentTimeMillis();
    try {
      Object callResult = Failsafe.with(sagaStep.getAction().getRetryPolicy()).get(action::call);
      evaluations.add(Evaluation.action(sagaStep.getAction().getName(), System.currentTimeMillis() - actionStart, true));
      return EvaluationResult.success(callResult);
    } catch (Throwable ta) {
      evaluations.add(Evaluation.action(sagaStep.getAction().getName(), System.currentTimeMillis() - actionStart, false));
      long compensationStart = System.currentTimeMillis();
      String compensation = null;
      try {
        while (!compensations.empty()) {
          SagaCompensation pop = compensations.pop();
          if (!pop.isTechnical()) {
            compensationStart = System.currentTimeMillis();
            compensation = pop.getName();
            Failsafe.with(pop.getRetryPolicy()).run(() -> pop.getCompensation().run());
            evaluations.add(Evaluation.compensation(compensation, System.currentTimeMillis() - compensationStart, true));
          }
        }
      } catch (Throwable tc) {
        evaluations.add(Evaluation.compensation(compensation, System.currentTimeMillis() - compensationStart, false));
        return EvaluationResult.failed(new SagaCompensationFailedException(compensation, sagaName, tc));
      }
      return EvaluationResult.failed(new SagaActionFailedException(sagaStep.getAction().getName(), sagaName, ta));
    }
  }
}
