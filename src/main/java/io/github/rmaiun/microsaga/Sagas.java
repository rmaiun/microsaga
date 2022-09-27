package io.github.rmaiun.microsaga;

import io.github.rmaiun.microsaga.func.CheckedFunction;
import io.github.rmaiun.microsaga.saga.Saga;
import io.github.rmaiun.microsaga.saga.SagaAction;
import io.github.rmaiun.microsaga.saga.SagaFlatMap;
import io.github.rmaiun.microsaga.saga.SagaSuccess;
import io.github.rmaiun.microsaga.support.NoResult;
import io.github.rmaiun.microsaga.saga.SagaCompensation;
import io.github.rmaiun.microsaga.util.SagaUtils;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Function;
import net.jodah.failsafe.RetryPolicy;

public class Sagas {

  private Sagas() {
  }

  public static <A> SagaAction<A> action(String name, Callable<A> action) {
    return new SagaAction<>(name, sagaId -> action.call(), SagaUtils.defaultRetryPolicy());
  }

  public static <A> SagaAction<A> action(String name, CheckedFunction<String, A> action) {
    return new SagaAction<>(name, action, SagaUtils.defaultRetryPolicy());
  }

  public static <A> SagaAction<A> retryableAction(String name, Callable<A> action, RetryPolicy<A> retryPolicy) {
    return new SagaAction<>(name, SagaUtils.callableToCheckedFunc(action), retryPolicy);
  }

  public static <A> SagaAction<A> retryableAction(String name, CheckedFunction<String, A> action, RetryPolicy<A> retryPolicy) {
    return new SagaAction<>(name, action, retryPolicy);
  }

  public static SagaAction<NoResult> voidAction(String name, Runnable action) {

    return new SagaAction<>(name, SagaUtils.runnableToCheckedFunc(action), SagaUtils.defaultRetryPolicy());
  }

  public static SagaAction<NoResult> voidAction(String name, Consumer<String> action) {

    return new SagaAction<>(name, SagaUtils.consumerToCheckedFunc(action), SagaUtils.defaultRetryPolicy());
  }

  public static SagaAction<NoResult> voidRetryableAction(String name, Runnable action, RetryPolicy<NoResult> retryPolicy) {
    return new SagaAction<>(name, SagaUtils.runnableToCheckedFunc(action), retryPolicy);
  }

  public static SagaAction<NoResult> voidRetryableAction(String name, Consumer<String> action, RetryPolicy<NoResult> retryPolicy) {
    return new SagaAction<>(name, SagaUtils.consumerToCheckedFunc(action), retryPolicy);
  }

  public static SagaCompensation compensation(String name, Runnable compensator) {
    return new SagaCompensation(name, compensator, SagaUtils.defaultRetryPolicy());
  }

  public static SagaCompensation compensation(String name, Consumer<String> compensator) {
    return new SagaCompensation(name, compensator, SagaUtils.defaultRetryPolicy());
  }

  public static SagaCompensation retryableCompensation(String name, Runnable compensator, RetryPolicy<Object> retryPolicy) {
    return new SagaCompensation(name, compensator, retryPolicy);
  }

  public static SagaCompensation retryableCompensation(String name, Consumer<String> compensator, RetryPolicy<Object> retryPolicy) {
    return new SagaCompensation(name, compensator, retryPolicy);
  }

  public static <A> Saga<A> success(A value) {
    return new SagaSuccess<>(value);
  }

  public static <A, B> Saga<B> flatMap(Saga<A> a, Function<A, Saga<B>> fB) {
    return new SagaFlatMap<>(a, fB);
  }
}
