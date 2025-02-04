package org.flow.orderflow.controller;

import lombok.AllArgsConstructor;
import org.flow.orderflow.model.Delivery;
import org.flow.orderflow.repository.DeliveryRepository;
import org.flow.orderflow.service.NovaPoshtaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/delivery")
@AllArgsConstructor  // Додано анотацію для автоматичного створення конструктора
public class DeliveryController {
  private final NovaPoshtaService novaPoshtaService;
  private final DeliveryRepository deliveryRepository;

  @GetMapping("/{trackingNumber}")
  public ResponseEntity<Delivery> getDelivery(@PathVariable String trackingNumber) {
    return ResponseEntity.ok(novaPoshtaService.getTrackingInfo(trackingNumber));
  }

  @GetMapping("/all")
  public ResponseEntity<List<Delivery>> getAllDeliveries() {
    return ResponseEntity.ok(deliveryRepository.findAll());
  }
}
