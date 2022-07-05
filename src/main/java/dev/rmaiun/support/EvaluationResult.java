package dev.rmaiun.support;

public class EvaluationResult<A> {

  private final A value;
  private final Throwable error;
  private final boolean compensationFailed = false;

  public static <X> EvaluationResult<X> success(X data) {
    return new EvaluationResult<>(data, null);
  }

  public static <X> EvaluationResult<X> actionFailed(Throwable t) {
    return new EvaluationResult<>(null, t);
  }

  public EvaluationResult(A value, Throwable error) {
    this.value = value;
    this.error = error;
  }

  public boolean isError() {
    return error != null;
  }

  public boolean isSuccess() {
    return error == null && value != null;
  }

  public A getValue() {
    return value;
  }

  public Throwable getError() {
    return error;
  }
}
