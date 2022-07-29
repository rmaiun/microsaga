package dev.rmaiun.saga4j;

import dev.rmaiun.saga4j.saga.Saga;
import dev.rmaiun.saga4j.saga.SagaAction;
import dev.rmaiun.saga4j.saga.SagaFlatMap;
import dev.rmaiun.saga4j.saga.SagaSuccess;
import dev.rmaiun.saga4j.support.SagaCompensation;
import java.util.concurrent.Callable;
import java.util.function.Function;
import net.jodah.failsafe.RetryPolicy;

public class Sagas {

  private Sagas() {
  }

  // todo: should work with void methods
  public static <A> SagaAction<A> action(String name, Callable<A> action) {
    return new SagaAction<>(name, action, new RetryPolicy<A>().withMaxRetries(0));
  }

  public static <A> SagaAction<A> retryableAction(String name, Callable<A> action, RetryPolicy<A> retryPolicy) {
    return new SagaAction<>(name, action, retryPolicy);
  }

  public static SagaCompensation compensation(String name, Runnable compensator) {
    return new SagaCompensation(name, compensator, new RetryPolicy<>().withMaxRetries(0));
  }

  public static SagaCompensation retryableCompensation(String name, Runnable compensator, RetryPolicy<Object> retryPolicy) {
    return new SagaCompensation(name, compensator, retryPolicy);
  }

  public static <A> Saga<A> success(A value) {
    return new SagaSuccess<>(value);
  }

  public static <A, B> Saga<B> flatMap(Saga<A> a, Function<A, Saga<B>> fB) {
    return new SagaFlatMap<>(a, fB);
  }
}
