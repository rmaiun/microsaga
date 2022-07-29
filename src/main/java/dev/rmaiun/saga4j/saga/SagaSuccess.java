package dev.rmaiun.saga4j.saga;



public class SagaSuccess<T> extends Saga<T> {

  private final T value;

  public SagaSuccess(T value) {
    this.value = value;
  }

  public T getValue() {
    return value;
  }
}
