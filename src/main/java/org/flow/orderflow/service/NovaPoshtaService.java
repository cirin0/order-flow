package org.flow.orderflow.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.flow.orderflow.model.Delivery;
import org.flow.orderflow.repository.DeliveryRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class NovaPoshtaService {
  @Value("${nova.poshta.api.key}")
  private String apiKey;

  @Value("${nova.poshta.api.url}")
  private String apiUrl;

  private final RestTemplate restTemplate;
  private final DeliveryRepository deliveryRepository;

  public NovaPoshtaService(RestTemplateBuilder restTemplateBuilder, DeliveryRepository deliveryRepository) {
    this.restTemplate = restTemplateBuilder.build();
    this.deliveryRepository = deliveryRepository;
  }

  public Delivery getTrackingInfo(String trackingNumber) {
    Map<String, Object> request = Map.of(
      "apiKey", apiKey,
      "modelName", "TrackingDocument",
      "calledMethod", "getStatusDocuments",
      "methodProperties", Map.of(
        "Documents", List.of(Map.of("DocumentNumber", trackingNumber))
      )
    );

    ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, request, String.class);

    try {
      JsonNode root = new ObjectMapper().readTree(response.getBody());
      JsonNode data = root.path("data").get(0);

      Delivery delivery = new Delivery();
      delivery.setTrackingNumber(trackingNumber);
      delivery.setStatus(data.path("Status").asText());
      delivery.setRecipient(data.path("RecipientFullName").asText());
      delivery.setUpdatedAt(LocalDateTime.now());

      return deliveryRepository.save(delivery);
    } catch (Exception e) {
      throw new RuntimeException("Помилка обробки відповіді API", e);
    }
  }
}
