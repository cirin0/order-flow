package org.flow.orderflow.exception;

public class InvalidRefreshToken extends RuntimeException {
  public InvalidRefreshToken(String message) {
    super(message);
  }
}
