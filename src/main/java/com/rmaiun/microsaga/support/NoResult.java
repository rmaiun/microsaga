package com.rmaiun.microsaga.support;

public final class NoResult {

  private static NoResult instance = null;

  private NoResult() {
  }

  public static NoResult instance() {
    if (instance == null) {
      instance = new NoResult();
    }
    return instance;
  }

  @Override
  public String toString() {
    return "NoResult{}";
  }
}
