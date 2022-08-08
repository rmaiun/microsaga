package io.github.rmaiun.microsaga.exception;

public class SagaCompensationFailedException extends RuntimeException {

  public SagaCompensationFailedException(String action, String saga, Throwable cause) {
    super(String.format("Compensation for saga %s is failed while compensates %s action ", saga, action), cause);
  }
}
