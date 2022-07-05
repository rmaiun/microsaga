package dev.rmaiun.support;

public class SagaCompensation {

  private final String name;
  private final Runnable compensation;

  public SagaCompensation(String name, Runnable compensation) {
    this.name = name;
    this.compensation = compensation;
  }

  public String getName() {
    return name;
  }

  public Runnable getCompensation() {
    return compensation;
  }
}
