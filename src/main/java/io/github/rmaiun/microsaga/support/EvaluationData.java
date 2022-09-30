package io.github.rmaiun.microsaga.support;

import java.util.Objects;
import java.util.StringJoiner;

public class EvaluationData<T> {

  private T data;
  private String classPath;

  public EvaluationData() {
  }

  public EvaluationData(T data, String classPath) {
    this.data = data;
    this.classPath = classPath;
  }

  public String getClassPath() {
    return classPath;
  }

  public void setClassPath(String classPath) {
    this.classPath = classPath;
  }

  public T getData() {
    return data;
  }

  public void setData(T data) {
    this.data = data;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof EvaluationData)) {
      return false;
    }
    EvaluationData<?> that = (EvaluationData<?>) o;
    return Objects.equals(data, that.data);
  }

  @Override
  public int hashCode() {
    return Objects.hash(data);
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", EvaluationData.class.getSimpleName() + "[", "]")
        .add("data=" + data)
        .toString();
  }
}
