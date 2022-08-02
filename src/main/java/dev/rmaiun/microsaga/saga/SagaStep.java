package dev.rmaiun.microsaga.saga;

import dev.rmaiun.microsaga.support.SagaCompensation;

public class SagaStep<A> extends Saga<A> {

  private final SagaAction<A> action;
  private final SagaCompensation compensator;

  public SagaStep(SagaAction<A> action, SagaCompensation compensator) {
    this.action = action;
    this.compensator = compensator;
  }

  public SagaAction<A> getAction() {
    return action;
  }

  public SagaCompensation getCompensator() {
    return compensator;
  }
}
