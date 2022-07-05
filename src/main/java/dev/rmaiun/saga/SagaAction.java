package dev.rmaiun.saga;

import dev.rmaiun.support.SagaCompensation;
import java.util.concurrent.Callable;

public class SagaAction<A> extends Saga<A> {

  private final String name;
  private final Callable<A> action;

  public SagaAction(String name, Callable<A> action) {
    this.name = name;
    this.action = action;
  }

  public SagaStep<A> compensate(SagaCompensation compensation) {
    return new SagaStep<>(this, compensation);
  }

  public String getName() {
    return name;
  }

  public Callable<A> getAction() {
    return action;
  }
}
