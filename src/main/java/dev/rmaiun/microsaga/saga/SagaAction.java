package dev.rmaiun.microsaga.saga;


import dev.rmaiun.microsaga.support.SagaCompensation;
import java.util.concurrent.Callable;
import net.jodah.failsafe.RetryPolicy;

public class SagaAction<A> extends Saga<A> {

  private final String name;
  private final RetryPolicy<A> retryPolicy;
  private final Callable<A> action;

  public SagaAction(String name, Callable<A> action, RetryPolicy<A> retryPolicy) {
    this.name = name;
    this.action = action;
    this.retryPolicy = retryPolicy;
  }

  public SagaStep<A> compensate(SagaCompensation compensation) {
    return new SagaStep<>(this, compensation);
  }

  public SagaStep<A> withoutCompensation() {
    return new SagaStep<>(this, SagaCompensation.technical());
  }

  public String getName() {
    return name;
  }

  public Callable<A> getAction() {
    return action;
  }

  public RetryPolicy<A> getRetryPolicy() {
    return retryPolicy;
  }
}
