package io.github.rmaiun.microsaga.support;

import java.util.Objects;

public class Evaluation {

  private final String name;
  private final EvaluationType evaluationType;
  private final long duration;
  private final boolean success;

  public Evaluation(String name, EvaluationType evaluationType, long duration, boolean success) {
    this.name = name;
    this.evaluationType = evaluationType;
    this.duration = duration;
    this.success = success;
  }

  public static Evaluation action(String name, long duration, boolean success) {
    return new Evaluation(name, EvaluationType.ACTION, duration, success);
  }

  public static Evaluation compensation(String name, long duration, boolean success) {
    return new Evaluation(name, EvaluationType.COMPENSATION, duration, success);
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

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("Evaluation{");
    sb.append("name='").append(name).append('\'');
    sb.append(", evaluationType=").append(evaluationType);
    sb.append(", duration=").append(duration);
    sb.append(", success=").append(success);
    sb.append('}');
    return sb.toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Evaluation)) {
      return false;
    }

    Evaluation that = (Evaluation) o;

    if (duration != that.duration) {
      return false;
    }
    if (success != that.success) {
      return false;
    }
    if (!Objects.equals(name, that.name)) {
      return false;
    }
    return evaluationType == that.evaluationType;
  }

  @Override
  public int hashCode() {
    int result = name != null ? name.hashCode() : 0;
    result = 31 * result + (evaluationType != null ? evaluationType.hashCode() : 0);
    result = 31 * result + (int) (duration ^ (duration >>> 32));
    result = 31 * result + (success ? 1 : 0);
    return result;
  }
}
