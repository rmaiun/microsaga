package io.github.rmaiun.microsaga.support;

import java.util.StringJoiner;

public class NoResult {

  public NoResult() {
  }

  public static NoResult instance() {
    return new NoResult();
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", NoResult.class.getSimpleName() + "[", "]")
        .toString();
  }
}
