package dev.rmaiun.exception;

public class SagaActionFailedException extends RuntimeException {

  public SagaActionFailedException(String action, String saga, Throwable cause) {
    super(String.format("Action %s for saga %s is failed", action, saga), cause);
  }
}
