package io.github.rmaiun.microsaga.support;

import java.util.function.Function;

public class EvaluationResult<A> {

  private final A value;
  private final EvaluationHistory evaluationHistory;
  private final RuntimeException error;

  public EvaluationResult(A value, RuntimeException error) {
    this.value = value;
    this.evaluationHistory = null;
    this.error = error;
  }

  public EvaluationResult(A value, EvaluationHistory evaluationHistory, RuntimeException error) {
    this.value = value;
    this.evaluationHistory = evaluationHistory;
    this.error = error;
  }

  public static <X> EvaluationResult<X> success(X data) {
    return new EvaluationResult<>(data, null, null);
  }

  public static <X> EvaluationResult<X> failed(RuntimeException t) {
    return new EvaluationResult<>(null, t);
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

  public EvaluationHistory getEvaluationHistory() {
    return evaluationHistory;
  }

  public A valueOrThrow() {
    if (isError()) {
      throw getError();
    }
    return getValue();
  }

  public <E extends RuntimeException> A valueOrThrow(Function<Throwable, E> errorTransformer) {
    if (isError()) {
      throw errorTransformer.apply(getError());
    }
    return getValue();
  }
}
