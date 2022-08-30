package io.github.rmaiun.microsaga.support;


import java.util.function.Consumer;
import net.jodah.failsafe.RetryPolicy;

public class SagaCompensation {

  private final String name;
  private final Consumer<String> compensation;
  private final RetryPolicy<Object> retryPolicy;
  private final boolean technical;

  public SagaCompensation(String name, Runnable compensation, RetryPolicy<Object> retryPolicy) {
    this.name = name;
    this.compensation = sagaId -> compensation.run();
    this.retryPolicy = retryPolicy;
    this.technical = false;
  }

  public SagaCompensation(String name, Consumer<String> compensation, RetryPolicy<Object> retryPolicy) {
    this.name = name;
    this.compensation = compensation;
    this.retryPolicy = retryPolicy;
    this.technical = false;
  }

  public SagaCompensation(String name, Runnable compensation, RetryPolicy<Object> retryPolicy, boolean technical) {
    this.name = name;
    this.compensation = sagaId -> compensation.run();
    ;
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

  public Consumer<String> getCompensation() {
    return compensation;
  }

  public RetryPolicy<Object> getRetryPolicy() {
    return retryPolicy;
  }

  public boolean isTechnical() {
    return technical;
  }
}
