package io.github.rmaiun.microsaga.support;

import java.util.List;
import java.util.Objects;

public class EvaluationHistory {

  private final String sagaName;
  private final List<Evaluation> evaluations;

  public EvaluationHistory(String sagaName, List<Evaluation> evaluations) {
    this.sagaName = sagaName;
    this.evaluations = evaluations;
  }

  public String getSagaName() {
    return sagaName;
  }

  public List<Evaluation> getEvaluations() {
    return evaluations;
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("EvaluationHistory{");
    sb.append("sagaName='").append(sagaName).append('\'');
    sb.append(", evaluations=").append(evaluations);
    sb.append('}');
    return sb.toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof EvaluationHistory)) {
      return false;
    }

    EvaluationHistory that = (EvaluationHistory) o;

    if (!Objects.equals(sagaName, that.sagaName)) {
      return false;
    }
    return Objects.equals(evaluations, that.evaluations);
  }

  @Override
  public int hashCode() {
    int result = sagaName != null ? sagaName.hashCode() : 0;
    result = 31 * result + (evaluations != null ? evaluations.hashCode() : 0);
    return result;
  }
}
