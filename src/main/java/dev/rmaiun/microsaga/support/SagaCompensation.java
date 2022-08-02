package dev.rmaiun.microsaga.support;


import net.jodah.failsafe.RetryPolicy;

public class SagaCompensation {

  private final String name;
  private final Runnable compensation;
  private final RetryPolicy<Object> retryPolicy;
  private final boolean technical;

  public SagaCompensation(String name, Runnable compensation, RetryPolicy<Object> retryPolicy) {
    this.name = name;
    this.compensation = compensation;
    this.retryPolicy = retryPolicy;
    this.technical = false;
  }

  public SagaCompensation(String name, Runnable compensation, RetryPolicy<Object> retryPolicy, boolean technical) {
    this.name = name;
    this.compensation = compensation;
    this.retryPolicy = retryPolicy;
    this.technical = technical;
  }

  public static SagaCompensation technical() {
    return new SagaCompensation("", () -> {
    }, new RetryPolicy<>().withMaxRetries(0), true);
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

  public boolean isTechnical() {
    return technical;
  }
}
