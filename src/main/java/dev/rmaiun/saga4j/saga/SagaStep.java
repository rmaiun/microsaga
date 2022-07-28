package dev.rmaiun.saga4j.saga;

import com.sun.istack.internal.NotNull;
import dev.rmaiun.saga4j.support.SagaCompensation;

public class SagaStep<A> extends Saga<A> {

  private final SagaAction<A> action;
  private final SagaCompensation compensator;

  public SagaStep(@NotNull SagaAction<A> action, @NotNull SagaCompensation compensator) {
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
