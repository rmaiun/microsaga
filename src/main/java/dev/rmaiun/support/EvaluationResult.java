package dev.rmaiun.support;

public class EvaluationResult<A> {

  private final A value;
  private final Throwable error;
  private final boolean compensationFailed;

  public static <X> EvaluationResult<X> success(X data) {
    return new EvaluationResult<>(data, null, false);
  }

  public static <X> EvaluationResult<X> actionFailed(Throwable t) {
    return new EvaluationResult<>(null, t, false);
  }

  public static <X> EvaluationResult<X> compensationFailed(Throwable t) {
    return new EvaluationResult<>(null, t, true);
  }

  public EvaluationResult(A value, Throwable error, boolean compensationFailed) {
    this.value = value;
    this.error = error;
    this.compensationFailed = compensationFailed;
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
