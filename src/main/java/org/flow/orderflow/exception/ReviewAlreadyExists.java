package org.flow.orderflow.exception;

public class ReviewAlreadyExists extends RuntimeException {
  public ReviewAlreadyExists(String message) {
    super(message);
  }
}
