package dev.rmaiun.saga4j.support;

import com.sun.istack.internal.NotNull;
import net.jodah.failsafe.RetryPolicy;

public class SagaCompensation {

  private final String name;
  private final Runnable compensation;
  private final RetryPolicy<Object> retryPolicy;
  private final boolean technical;

  public SagaCompensation(@NotNull String name, @NotNull Runnable compensation, @NotNull RetryPolicy<Object> retryPolicy) {
    this.name = name;
    this.compensation = compensation;
    this.retryPolicy = retryPolicy;
    this.technical = false;
  }

  public SagaCompensation(@NotNull String name, @NotNull Runnable compensation, @NotNull RetryPolicy<Object> retryPolicy, boolean technical) {
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
