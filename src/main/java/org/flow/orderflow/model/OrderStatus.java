package org.flow.orderflow.model;

import lombok.Getter;

@Getter
public enum OrderStatus {
  NEW("Нове замовлення"),
  PROCESSING("В обробці"),
  SHIPPED("Відправлено"),
  DELIVERED("Доставлено"),
  CANCELLED("Скасовано");

  private final String description;

  OrderStatus(String description) {
    this.description = description;
  }
}
