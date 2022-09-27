package io.github.simpleservice.helper;

import io.github.rmaiun.microsaga.Sagas;
import io.github.rmaiun.microsaga.saga.SagaStep;
import io.github.simpleservice.domain.SagaInvocation;
import io.github.simpleservice.dto.CreateOrderDto;
import io.github.simpleservice.dto.OrderCreatedDto;
import io.github.simpleservice.service.OrderService;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class OrderSagaHelper {

  private final OrderService orderService;

  public OrderSagaHelper(OrderService orderService) {
    this.orderService = orderService;
  }

  public SagaStep<OrderCreatedDto> createOrderSagaStep(String user, String product, List<SagaInvocation> invocationList) {
    var action = Sagas.action("createOrder",
        sagaId -> orderService.createOrder(new CreateOrderDto(user, product, sagaId)));
    var compensation = Sagas.compensation("cancelOrder", orderService::cancelOrder);
    return action.compensate(compensation);
  }
}
