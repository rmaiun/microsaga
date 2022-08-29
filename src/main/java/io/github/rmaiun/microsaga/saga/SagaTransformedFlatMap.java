package io.github.rmaiun.microsaga.saga;

import io.github.rmaiun.microsaga.func.StubInputFunction;
import java.util.function.BiFunction;
import java.util.function.Function;

public class SagaTransformedFlatMap<A, B, C> extends Saga<C> {

  private final StubInputFunction<Saga<A>> rootSaga;
  private final Function<A, Saga<B>> sagaFunc;
  private final BiFunction<A, B, C> transformer;

  public SagaTransformedFlatMap(StubInputFunction<Saga<A>> saga, Function<A, Saga<B>> sagaFunc, BiFunction<A, B, C> transformer) {
    this.rootSaga = saga;
    this.sagaFunc = sagaFunc;
    this.transformer = transformer;
  }

  public Function<A, Saga<B>> getSagaFunc() {
    return sagaFunc;
  }

  public BiFunction<A, B, C> getTransformer() {
    return transformer;
  }

  public StubInputFunction<Saga<A>> getRootSaga() {
    return rootSaga;
  }
}
