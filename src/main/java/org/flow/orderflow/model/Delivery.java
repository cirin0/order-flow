package org.flow.orderflow.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Delivery {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String trackingNumber;
  private String status;
  private String recipient;
  private String city;
  private String warehouse;
  private String cityRef;
  private String warehouseRef;
  private LocalDateTime updatedAt;
}
