package io.github.simpleservice.configuration;

import io.github.rmaiun.microsaga.exception.SagaActionFailedException;
import io.github.rmaiun.microsaga.exception.SagaCompensationFailedException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(value = { SagaCompensationFailedException.class })
  protected ResponseEntity<Object> handleConflict(
      RuntimeException ex, WebRequest request) {
    String bodyOfResponse = "Compensation failed";
    return handleExceptionInternal(ex, new ErrorDto(bodyOfResponse, ex.getMessage()),
        new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
  }

  @ExceptionHandler(value = { SagaActionFailedException.class })
  protected ResponseEntity<Object> handleConflict2(
      RuntimeException ex, WebRequest request) {
    String bodyOfResponse = "Action failed";
    return handleExceptionInternal(ex, new ErrorDto(bodyOfResponse, ex.getMessage()),
        new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
  }

  public record ErrorDto(String reason, String message) {

  }

}
