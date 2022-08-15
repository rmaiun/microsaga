package io.github.simpleservice.controller;

import io.github.simpleservice.dto.CreateOrderDto;
import io.github.simpleservice.dto.OrderCreatedDto;
import io.github.simpleservice.service.OrderService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order")
public class OrderController {

  private final OrderService orderService;

  public OrderController(OrderService orderService) {
    this.orderService = orderService;
  }

  @PostMapping("/create")
  public OrderCreatedDto createOrder(@RequestBody CreateOrderDto dto) {
    return orderService.createOrder(dto);
  }

  @PostMapping("/cancel")
  public void cancelOrder(@RequestParam("sagaId") String sagaId) {
    orderService.cancelOrder(sagaId);
  }

}
