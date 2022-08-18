package io.github.rmaiun.microsaga.saga;

import java.util.function.BiFunction;
import java.util.function.Function;

public class SagaTransformedFlatMap<A, B, C> extends SagaFlatMap<A, B> {

  private final BiFunction<A, B, C> transformer;

  public SagaTransformedFlatMap(Saga<A> a, Function<A, Saga<B>> fB, BiFunction<A, B, C> transformer) {
    super(a, fB);
    this.transformer = transformer;
  }

  public BiFunction<A, B, C> getTransformer() {
    return transformer;
  }
}
