package org.flow.orderflow.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "addresses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(nullable = false)
  private Long id;

  private String region;
  private String city;
  private String area;
  private String street;
  private String apartment;
  private String house;

  @OneToOne(mappedBy = "deliveryAddress")
  private User user;
}
