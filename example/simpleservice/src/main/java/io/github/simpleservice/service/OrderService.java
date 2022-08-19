package io.github.simpleservice.service;

import io.github.simpleservice.domain.Order;
import io.github.simpleservice.dto.CreateOrderDto;
import io.github.simpleservice.dto.OrderCreatedDto;
import io.github.simpleservice.repository.OrderRepository;
import java.time.ZonedDateTime;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

@Service
public class OrderService {
  public static final Logger LOG = LogManager.getLogger(OrderService.class);

  private final OrderRepository orderRepository;


  public OrderService(OrderRepository orderRepository) {
    this.orderRepository = orderRepository;
  }

  public OrderCreatedDto createOrder(CreateOrderDto dto) {
    LOG.info("Calling OrderService.createOrder");
    var order = new Order();
    order.setProduct(dto.product());
    order.setSagaId(dto.sagaId());
    var result = orderRepository.save(order);
    var timestamp = dto.product().startsWith("PS")
        ? ZonedDateTime.now().plusMinutes(30)
        : ZonedDateTime.now();
    return new OrderCreatedDto(result.getId(), 500L, dto.user(), timestamp);
  }

  public void cancelOrder(String sagaId) {
    LOG.info("Calling OrderService.cancelOrder");
    orderRepository.deleteAllBySagaId(sagaId);
  }
}
