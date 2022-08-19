package io.github.simpleservice.helper;

import io.github.rmaiun.microsaga.Sagas;
import io.github.rmaiun.microsaga.saga.SagaStep;
import io.github.simpleservice.dto.CreateOrderDto;
import io.github.simpleservice.dto.OrderCreatedDto;
import io.github.simpleservice.service.OrderService;
import org.springframework.stereotype.Service;

@Service
public class OrderSagaHelper {

  private final OrderService orderService;

  public OrderSagaHelper(OrderService orderService) {
    this.orderService = orderService;
  }

  public SagaStep<OrderCreatedDto> createOrderSagaStep(String user, String sagaId, String product) {
    var action = Sagas.action("cancelSaga",
        () -> orderService.createOrder(new CreateOrderDto(user, product, sagaId)));
    var compensation = Sagas.compensation("cancelOrder",
        () -> orderService.cancelOrder(sagaId));
    return action.compensate(compensation);
  }
}
