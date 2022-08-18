package io.github.rmaiun.microsaga.support;

import io.github.rmaiun.microsaga.saga.Saga;
import java.util.function.BiFunction;
import java.util.function.Function;

public class FunctionContext {

  private Function<Object, Saga<Object>> sagaFunction;
  private BiFunction<Object, Object, Object> transformer;

  public FunctionContext(Function<Object, Saga<Object>> sagaFunction) {
    this.sagaFunction = sagaFunction;
    this.transformer = null;
  }

  public FunctionContext(Function<Object, Saga<Object>> sagaFunction, BiFunction<Object, Object, Object> transformer) {
    this.sagaFunction = sagaFunction;
    this.transformer = transformer;
  }

  public Function<Object, Saga<Object>> getSagaFunction() {
    return sagaFunction;
  }

  public BiFunction<Object, Object, Object> getTransformer() {
    return transformer;
  }
}
