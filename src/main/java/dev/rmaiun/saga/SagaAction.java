package dev.rmaiun.saga;

import com.sun.istack.internal.NotNull;
import dev.rmaiun.support.SagaCompensation;
import java.util.concurrent.Callable;
import net.jodah.failsafe.RetryPolicy;

public class SagaAction<A> extends Saga<A> {

  private final String name;
  private final RetryPolicy<A> retryPolicy;
  private final Callable<A> action;

  public SagaAction(@NotNull String name, @NotNull Callable<A> action, @NotNull RetryPolicy<A> retryPolicy) {
    this.name = name;
    this.action = action;
    this.retryPolicy = retryPolicy;
  }

  public SagaStep<A> compensate(SagaCompensation compensation) {
    return new SagaStep<>(this, compensation);
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
