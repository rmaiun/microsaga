package dev.rmaiun.saga;

import com.sun.istack.internal.NotNull;

public class SagaSuccess<T> extends Saga<T> {

  private T value;

  public SagaSuccess(@NotNull T value) {
    this.value = value;
  }

  public T getValue() {
    return value;
  }
}
