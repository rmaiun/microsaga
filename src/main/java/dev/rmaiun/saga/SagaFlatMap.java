package dev.rmaiun.saga;

import dev.rmaiun.func.StubInputFunction;
import java.util.function.Function;

public class SagaFlatMap<A, B> extends Saga<B> {

  private StubInputFunction<Saga<A>> a;
  private Function<A, Saga<B>> fB;

  public SagaFlatMap(Saga<A> a, Function<A, Saga<B>> fB) {
    this.a = x -> a;
    this.fB = fB ;
  }

  public StubInputFunction<Saga<A>> getA() {
    return a;
  }

  public Function<A, Saga<B>> getfB() {
    return fB;
  }
}
