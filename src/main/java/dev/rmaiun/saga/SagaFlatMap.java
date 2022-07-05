package dev.rmaiun.saga;

import java.util.function.Function;

public class SagaFlatMap<A, B> extends Saga<B> {

  private Saga<A> a;
  private Function<A, Saga<B>> fB;

  public SagaFlatMap(Saga<A> a, Function<A, Saga<B>> fB) {
    this.a = a;
    this.fB = fB;
  }

  public Saga<A> getA() {
    return a;
  }

  public Function<A, Saga<B>> getfB() {
    return fB;
  }
}
