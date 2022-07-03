package dev.rmaiun;

public class SagaSuccess<T> extends Saga<T> {

  private T value;

  public SagaSuccess(T value) {
    this.value = value;
  }

  public T getValue() {
    return value;
  }
}
