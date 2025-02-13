package org.flow.orderflow.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
  @ExceptionHandler(NotFound.class)
  public ResponseEntity<ErrorResponse> handleNotFound(NotFound e) {
    ErrorResponse errorResponse = new ErrorResponse(
      HttpStatus.NOT_FOUND,
      e.getMessage()
    );
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
  }
}
