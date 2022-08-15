package io.github.simpleservice.controller;

import io.github.simpleservice.dto.PlanDeliveryDto;
import io.github.simpleservice.service.DeliveryService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/delivery")
public class DeliveryController {

  private final DeliveryService deliveryService;

  public DeliveryController(DeliveryService deliveryService) {
    this.deliveryService = deliveryService;
  }

  @PostMapping("/plan")
  public void planDelivery(PlanDeliveryDto dto) {
    deliveryService.planDelivery(dto);
  }
}
