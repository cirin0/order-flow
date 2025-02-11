package org.flow.orderflow.exception;

public class PaymentException extends RuntimeException {
  public PaymentException(String message) {
    super(message);
  }
}
