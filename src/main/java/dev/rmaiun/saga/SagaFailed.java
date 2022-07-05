package dev.rmaiun.saga;

public class SagaFailed<A> extends Saga<A> {

  private final Throwable cause;

  public SagaFailed(Throwable cause) {
    this.cause = cause;
  }

  public Throwable getCause() {
    return cause;
  }
}
