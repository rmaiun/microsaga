package dev.rmaiun;

import java.util.concurrent.Callable;
import java.util.function.Function;

public class Sagas {

  private Sagas() {
  }

  public static <A> Saga<A> step(Callable<A> action, Function<A, Runnable> compensator) {
    return new SagaStep<>(action, compensator);
  }

  public static <A> Saga<A> success(A value) {
    return new SagaSuccess<>(value);
  }

  public static <A, B> Saga<B> flatMap(Saga<A> a, Function<A, Saga<B>> fB) {
    return new SagaFlatMap<>(a, fB);
  }
}
