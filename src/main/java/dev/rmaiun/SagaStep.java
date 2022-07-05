package dev.rmaiun;

import java.util.concurrent.Callable;

public class SagaStep<A> extends Saga<A> {

  private String name;
  private Callable<A> action;
  private Runnable compensator;

  public SagaStep(String name, Callable<A> action, Runnable compensator) {
    this.name = name;
    this.action = action;
    this.compensator = compensator;
  }

  public String getName() {
    return name;
  }

  public Callable<A> getAction() {
    return action;
  }

  public Runnable getCompensator() {
    return compensator;
  }
}
