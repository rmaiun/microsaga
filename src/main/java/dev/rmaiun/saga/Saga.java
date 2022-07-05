package dev.rmaiun.saga;

import dev.rmaiun.Sagas;
import java.util.function.Function;

public abstract class Saga<A> {

  public <B> Saga<B> map(Function<A, B> f) {
    return flatmap(a -> Sagas.success(f.apply(a)));
  }

  public <B> Saga<B> flatmap(Function<A, Saga<B>> f) {
    return Sagas.flatMap(this, f);
  }
}
