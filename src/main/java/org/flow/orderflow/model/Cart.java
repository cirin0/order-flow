package org.flow.orderflow.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "carts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cart {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", unique = true)
  private User user;

  @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private List<CartItem> items = new ArrayList<>();

  @Builder.Default
  private Double totalPrice = 0.0;

  @Transient
  @Builder.Default
  private List<String> warningMessages = new ArrayList<>();

  public void recalculateTotal() {
    totalPrice = items
      .stream()
      .mapToDouble(item -> item.getProduct().getPrice() * item.getQuantity())
      .sum();
  }

  public void addWarningMessage(String message) {
    this.warningMessages.add(message);
  }
}
