package io.github.rmaiun.microsaga.saga;

import io.github.rmaiun.microsaga.func.CheckedFunction;
import io.github.rmaiun.microsaga.util.SagaUtils;
import java.util.function.Consumer;
import net.jodah.failsafe.RetryPolicy;

public class SagaAction<A> extends Saga<A> {

  private final String name;
  private final RetryPolicy<A> retryPolicy;
  private final CheckedFunction<String, A> action;

  public SagaAction(String name, CheckedFunction<String, A> action, RetryPolicy<A> retryPolicy) {
    this.name = name;
    this.action = action;
    this.retryPolicy = retryPolicy;
  }

  public SagaStep<A> compensate(SagaCompensation compensation) {
    return new SagaStep<>(this, compensation);
  }

  public SagaStep<A> compensate(String name, Runnable compensation) {
    return new SagaStep<>(this, new SagaCompensation(name, compensation, SagaUtils.defaultRetryPolicy()));
  }

  public SagaStep<A> compensate(String name, Runnable compensation, RetryPolicy<Object> retryPolicy) {
    return new SagaStep<>(this, new SagaCompensation(name, compensation, retryPolicy));
  }

  public SagaStep<A> compensate(String name, Consumer<String> compensation) {
    return new SagaStep<>(this, new SagaCompensation(name, compensation, SagaUtils.defaultRetryPolicy()));
  }

  public SagaStep<A> compensate(String name, Consumer<String> compensation, RetryPolicy<Object> retryPolicy) {
    return new SagaStep<>(this, new SagaCompensation(name, compensation, retryPolicy));
  }

  public SagaStep<A> withoutCompensation() {
    return new SagaStep<>(this, SagaCompensation.technical());
  }

  public String getName() {
    return name;
  }

  public CheckedFunction<String, A> getAction() {
    return action;
  }

  public RetryPolicy<A> getRetryPolicy() {
    return retryPolicy;
  }
}
