package dev.rmaiun.support;

import net.jodah.failsafe.RetryPolicy;

public class SagaCompensation {

  private final String name;
  private final Runnable compensation;
  private final RetryPolicy<Object> retryPolicy;

  public SagaCompensation(String name, Runnable compensation, RetryPolicy<Object> retryPolicy) {
    this.name = name;
    this.compensation = compensation;
    this.retryPolicy = retryPolicy;
  }

  public String getName() {
    return name;
  }

  public Runnable getCompensation() {
    return compensation;
  }

  public RetryPolicy<Object> getRetryPolicy() {
    return retryPolicy;
  }
}
