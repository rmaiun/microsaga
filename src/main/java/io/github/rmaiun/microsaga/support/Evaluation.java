package io.github.rmaiun.microsaga.support;

import java.util.Objects;
import java.util.StringJoiner;

public class Evaluation<A> {

  private final String name;
  private final EvaluationType evaluationType;
  private final long duration;
  private final boolean success;

  private final EvaluationData<A> result;

  public Evaluation(String name, EvaluationType evaluationType, long duration, boolean success, A data) {
    this.name = name;
    this.evaluationType = evaluationType;
    this.duration = duration;
    this.success = success;
    this.result = new EvaluationData<>(data);
  }

  public static <B> Evaluation<B> action(String name, long duration, boolean success, B result) {
    return new Evaluation<>(name, EvaluationType.ACTION, duration, success, result);
  }

  public static <B> Evaluation<B> compensation(String name, long duration, boolean success, B result) {
    return new Evaluation<>(name, EvaluationType.COMPENSATION, duration, success, result);
  }

  public String getName() {
    return name;
  }

  public EvaluationType getEvaluationType() {
    return evaluationType;
  }

  public long getDuration() {
    return duration;
  }

  public boolean isSuccess() {
    return success;
  }

  public EvaluationData<A> getResult() {
    return result;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Evaluation)) {
      return false;
    }
    Evaluation<?> that = (Evaluation<?>) o;
    return duration == that.duration && success == that.success && Objects.equals(name, that.name) && evaluationType == that.evaluationType
        && Objects.equals(result, that.result);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, evaluationType, duration, success, result);
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", Evaluation.class.getSimpleName() + "[", "]")
        .add("name='" + name + "'")
        .add("evaluationType=" + evaluationType)
        .add("duration=" + duration)
        .add("success=" + success)
        .add("result=" + result)
        .toString();
  }
}
