package io.github.rmaiun.microsaga.support;

import io.github.rmaiun.microsaga.exception.SagaAdaptedException;
import java.util.function.Consumer;
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

  public void orElseThrow() {
    if (isError()) {
      throw getError();
    }
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

  public EvaluationResult<A> peek(Consumer<EvaluationResult<A>> consumer) {
    consumer.accept(this);
    return this;
  }

  public EvaluationResult<A> peekValue(Consumer<A> consumer) {
    if (this.isSuccess()) {
      consumer.accept(this.getValue());
    }
    return this;
  }

  public EvaluationResult<A> peekError(Consumer<RuntimeException> consumer) {
    if (this.isError()) {
      consumer.accept(this.getError());
    }
    return this;
  }

  public <B> B fold(Function<A, B> valueTransformer, Function<RuntimeException, B> errorTransformer) {
    if (this.isError()) {
      return errorTransformer.apply(error);
    } else {
      return valueTransformer.apply(value);
    }
  }

  public EvaluationResult<A> adaptError(Function<RuntimeException, A> errorAdapter) {
    if (this.isError()) {
      try {
        A value = errorAdapter.apply(this.error);
        return new EvaluationResult<>(value, this.evaluationHistory, null);
      } catch (RuntimeException t) {
        return new EvaluationResult<>(null, this.evaluationHistory, t);
      } catch (Throwable t) {
        return new EvaluationResult<>(null, this.evaluationHistory, new SagaAdaptedException(t));
      }
    } else {
      return this;
    }
  }
}
