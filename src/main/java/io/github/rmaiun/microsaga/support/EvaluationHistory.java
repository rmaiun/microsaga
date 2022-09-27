package io.github.rmaiun.microsaga.support;

import java.util.List;
import java.util.Objects;

public class EvaluationHistory {

  private final String sagaId;
  private final List<Evaluation<?>> evaluations;

  public EvaluationHistory(String sagaId, List<Evaluation<?>> evaluations) {
    this.sagaId = sagaId;
    this.evaluations = evaluations;
  }

  public String getSagaId() {
    return sagaId;
  }

  public List<Evaluation<?>> getEvaluations() {
    return evaluations;
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("EvaluationHistory{");
    sb.append("sagaName='").append(sagaId).append('\'');
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

    if (!Objects.equals(sagaId, that.sagaId)) {
      return false;
    }
    return Objects.equals(evaluations, that.evaluations);
  }

  @Override
  public int hashCode() {
    int result = sagaId != null ? sagaId.hashCode() : 0;
    result = 31 * result + (evaluations != null ? evaluations.hashCode() : 0);
    return result;
  }
}
