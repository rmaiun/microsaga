package io.github.simpleservice.controller;

import io.github.simpleservice.dto.PaymentProcessedDto;
import io.github.simpleservice.dto.ProcessPaymentDto;
import io.github.simpleservice.service.PaymentService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payment")
public class PaymentController {

  private final PaymentService paymentService;

  public PaymentController(PaymentService paymentService) {
    this.paymentService = paymentService;
  }

  @PostMapping("/process/{city}")
  public PaymentProcessedDto createOrder(@RequestBody ProcessPaymentDto dto, @PathVariable("city") String city) {
    return paymentService.processPayment(dto, city);
  }

  @PostMapping("/cancel")
  public void cancelOrder(@RequestParam("sagaId") String sagaId) {
    paymentService.cancelPayment(sagaId);
  }

}
