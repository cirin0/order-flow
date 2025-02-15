package org.flow.orderflow.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "delivery_addresses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryAddress {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String region;
  private String city;
  private String area;
  private String street;
  private String house;
  private String apartment;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "order_id", nullable = false)
  private Order order;

}
