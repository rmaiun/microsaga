package io.github.simpleservice.service;

import io.github.simpleservice.domain.Order;
import io.github.simpleservice.dto.CreateOrderDto;
import io.github.simpleservice.dto.OrderCreatedDto;
import io.github.simpleservice.repository.OrderRepository;
import java.time.ZonedDateTime;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

  private final OrderRepository orderRepository;


  public OrderService(OrderRepository orderRepository) {
    this.orderRepository = orderRepository;
  }

  public OrderCreatedDto createOrder(CreateOrderDto dto) {
    var order = new Order();
    order.setProduct(dto.product());
    order.setSagaId(dto.sagaId());
    var result = orderRepository.save(order);
    var timestamp = dto.product().startsWith("PS")
        ? ZonedDateTime.now().plusMinutes(30)
        : ZonedDateTime.now();
    return new OrderCreatedDto(result.getId(), timestamp);
  }

  public void cancelOrder(String sagaId) {
    orderRepository.deleteAllBySagaId(sagaId);
  }
}
