package dev.rmaiun;

import java.util.function.Function;

public class SagaStep<A, B> {

  private Saga<A> current;
  private Saga<B> prev;

  public SagaStep(Saga<A> current, Saga<B> prev) {
    this.current = current;
    this.prev = prev;
  }

  public Saga<A> getCurrent() {
    return current;
  }

  public Saga<B> getPrev() {
    return prev;
  }

  public SagaStep<A,B> then(Function<A,Saga<B>> f) {

  }
}
