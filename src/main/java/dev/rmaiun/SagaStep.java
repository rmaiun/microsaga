package dev.rmaiun;

import java.util.concurrent.Callable;
import java.util.function.Function;

public class SagaStep<A> extends Saga<A> {

  private Callable<A> action;
  private Runnable compensator;

  public SagaStep(Callable<A> action, Runnable compensator) {
    this.action = action;
    this.compensator = compensator;
  }

  public Callable<A> getAction() {
    return action;
  }

  public Runnable getCompensator() {
    return compensator;
  }
}
