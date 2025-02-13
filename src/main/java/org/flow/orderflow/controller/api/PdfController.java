package org.flow.orderflow.controller.api;

import lombok.RequiredArgsConstructor;
import org.flow.orderflow.dto.order.OrderDto;
import org.flow.orderflow.service.PdfService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/invoice")
@RequiredArgsConstructor
public class PdfController {
  private final PdfService pdfService;

  @GetMapping("/order/{orderId}/invoice")
  public ResponseEntity<byte[]> downloadInvoice(@PathVariable Long orderId,
                                                @RequestBody OrderDto order) {
    byte[] pdf = pdfService.generateInvoice(order);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_PDF);
    headers.setContentDispositionFormData("filename", "invoice-" + orderId + ".pdf");
    return new ResponseEntity<>(pdf, headers, HttpStatus.OK);
  }

}
