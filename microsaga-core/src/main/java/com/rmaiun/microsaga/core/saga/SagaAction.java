package com.rmaiun.microsaga.core.saga;


import com.rmaiun.microsaga.core.support.SagaCompensation;
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

  public SagaStep<A> compensate(String name, Runnable compensation) {
    return new SagaStep<>(this, new SagaCompensation(name, compensation, new RetryPolicy<>().withMaxRetries(0)));
  }

  public SagaStep<A> compensate(String name, Runnable compensation, RetryPolicy<Object> retryPolicy) {
    return new SagaStep<>(this, new SagaCompensation(name, compensation, retryPolicy));
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
