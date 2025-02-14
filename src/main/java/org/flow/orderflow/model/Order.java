package org.flow.orderflow.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private List<OrderItem> items = new ArrayList<>();

  private Double totalPrice;

  @Enumerated(EnumType.STRING)
  @Builder.Default
  private OrderStatus status = OrderStatus.NEW;

  @Column(unique = true, nullable = true, length = 10)
  private String orderNumber;

  @Column(nullable = false)
  private LocalDateTime orderDate;

  @PrePersist
  protected void onCreate() {
    this.orderDate = LocalDateTime.now();
  }

//  @OneToOne(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
//  private OrderDetails orderDetails;
}
