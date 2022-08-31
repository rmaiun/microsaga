package io.github.rmaiun.microsaga.saga;

import io.github.rmaiun.microsaga.Sagas;
import java.util.function.BiFunction;
import java.util.function.Function;

public abstract class Saga<A> {

  public <B> Saga<B> map(Function<A, B> f) {
    return flatmap(a -> Sagas.success(f.apply(a)));
  }

  public <B> Saga<B> flatmap(Function<A, Saga<B>> f) {
    return Sagas.flatMap(this, f);
  }

  public <B> Saga<B> then(Saga<B> b) {
    return Sagas.flatMap(this, a -> b);
  }

  public <B, C> Saga<C> zipWith(Function<A, Saga<B>> fB, BiFunction<A, B, C> transformer) {
    return new SagaTransformedFlatMap<>(x -> this, fB, transformer);
  }

  public <B, C> Saga<C> zipWith(Saga<B> b, Function<B, C> transformer) {
    return new SagaTransformedFlatMap<>(x -> this, a -> b, (in, out) -> transformer.apply(out));
  }

}
