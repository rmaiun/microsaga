package dev.rmaiun.microsaga.support;

public class EvaluationResult<A> {

  private final A value;
  private final RuntimeException error;

  public static <X> EvaluationResult<X> success(X data) {
    return new EvaluationResult<>(data, null);
  }

  public static <X> EvaluationResult<X> failed(RuntimeException t) {
    return new EvaluationResult<>(null, t);
  }

  public static <X> EvaluationResult<X> compensationFailed(RuntimeException t) {
    return new EvaluationResult<>(null, t);
  }

  public EvaluationResult(A value, RuntimeException error) {
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

  public RuntimeException getError() {
    return error;
  }
}
