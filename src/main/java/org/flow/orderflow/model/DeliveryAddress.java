package org.flow.orderflow.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "delivery_address")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryAddress {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "order_id", nullable = false)
  private Order order;

  @Column(name = "region")
  private String region;

  @Column(name = "city")
  private String city;

  @Column(name = "area")
  private String area;

  @Column(name = "street")
  private String street;

  @Column(name = "house")
  private String house;

  @Column(name = "apartment")
  private String apartment;

  @Column(name = "post_office")
  private String postOffice;
}
