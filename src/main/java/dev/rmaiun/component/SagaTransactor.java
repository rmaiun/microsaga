package dev.rmaiun.component;

import dev.rmaiun.exception.SagaActionFailedException;
import dev.rmaiun.exception.SagaCompensationFailedException;
import dev.rmaiun.saga.Saga;
import dev.rmaiun.saga.SagaFlatMap;
import dev.rmaiun.saga.SagaStep;
import dev.rmaiun.saga.SagaSuccess;
import dev.rmaiun.support.EvaluationResult;
import dev.rmaiun.support.LogVal;
import dev.rmaiun.support.SagaCompensation;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.Callable;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.jodah.failsafe.Failsafe;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SagaTransactor {

  private static final Logger LOG = LogManager.getLogger(SagaTransactor.class);

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
    List<LogVal> logVals = new ArrayList<>();
    EvaluationResult<X> result = run(sagaName, saga, new Stack<>(), logVals);
    StringBuilder sb = new StringBuilder(System.lineSeparator())
        .append(String.format("------------------------------------------------------------%n"))
        .append(String.format("sagaName = %s%n", sagaName));
    String logResult = logVals.stream()
        .map(lv -> String.format("[%s] %s %d(ms)", lv.getType(), lv.getName(), lv.getMs()))
        .collect(Collectors.joining(System.lineSeparator()));
    sb.append(logResult)
        .append(System.lineSeparator())
        .append(String.format("------------------------------------------------------------%n"));
    LOG.info(sb.toString());
    return result;
  }

  public <X, Y> EvaluationResult<X> run(String sagaName, Saga<X> saga, Stack<SagaCompensation> compensations, List<LogVal> logVals) {
    if (saga instanceof SagaSuccess) {
      return EvaluationResult.success(((SagaSuccess<X>) saga).getValue());
    } else if (saga instanceof SagaStep) {
      SagaStep<X> sagaStep = (SagaStep<X>) saga;
      Callable<X> action = sagaStep.getAction().getAction();
      compensations.add(sagaStep.getCompensator());
      long actionStart = System.currentTimeMillis();
      try {
        X callResult = Failsafe.with(sagaStep.getAction().getRetryPolicy()).get(action::call);
        logVals.add(LogVal.action(sagaStep.getAction().getName(), System.currentTimeMillis() - actionStart));
        return EvaluationResult.success(callResult);
      } catch (Throwable ta) {
        logVals.add(LogVal.action(sagaStep.getAction().getName(), System.currentTimeMillis() - actionStart));
        long compensationStart = System.currentTimeMillis();
        String compensation = null;
        try {
          while (!compensations.empty()) {
            SagaCompensation pop = compensations.pop();
            compensationStart = System.currentTimeMillis();
            compensation = pop.getName();
            Failsafe.with(pop.getRetryPolicy()).run(() -> pop.getCompensation().run());
            logVals.add(LogVal.compensation(compensation, System.currentTimeMillis() - compensationStart));
          }
        } catch (Throwable tc) {
          logVals.add(LogVal.compensation(compensation, System.currentTimeMillis() - compensationStart));
          return EvaluationResult.failed(new SagaCompensationFailedException(compensation, sagaName, tc));
        }
        return EvaluationResult.failed(new SagaActionFailedException(sagaStep.getAction().getName(), sagaName, ta));
      }
    } else if (saga instanceof SagaFlatMap) {
      SagaFlatMap<Y, X> sagaFlatMap = (SagaFlatMap<Y, X>) saga;
      EvaluationResult<Y> runA = run(sagaName, sagaFlatMap.getA(), compensations, logVals);
      return runA.isSuccess()
          ? run(sagaName, sagaFlatMap.getfB().apply(runA.getValue()), compensations, logVals)
          : EvaluationResult.failed(runA.getError());
    } else {
      return EvaluationResult.failed(new IllegalArgumentException("Invalid Saga Operation"));
    }
  }
}
