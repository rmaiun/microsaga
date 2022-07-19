package dev.rmaiun.support;

import com.sun.istack.internal.NotNull;
import net.jodah.failsafe.RetryPolicy;

public class SagaCompensation {

  private final String name;
  private final Runnable compensation;
  private final RetryPolicy<Object> retryPolicy;

  public SagaCompensation(@NotNull String name, @NotNull Runnable compensation, @NotNull RetryPolicy<Object> retryPolicy) {
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
