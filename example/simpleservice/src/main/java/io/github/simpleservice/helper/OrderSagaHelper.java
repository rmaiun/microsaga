package io.github.simpleservice.helper;

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
  private final SagaRequestHelper sagaRequestHelper;

  public OrderSagaHelper(OrderService orderService, SagaRequestHelper sagaRequestHelper) {
    this.orderService = orderService;
    this.sagaRequestHelper = sagaRequestHelper;
  }

  public SagaStep<OrderCreatedDto> createOrderSagaStep(String user, String product, List<SagaInvocation> invocationList) {
    var action = sagaRequestHelper.mkAction(
        "createOrder",
        sagaId -> orderService.createOrder(new CreateOrderDto(user, product, sagaId)),
        invocationList);
    var compensation = sagaRequestHelper.mkCompensation("cancelOrder", orderService::cancelOrder, invocationList);
    return action.compensate(compensation);
  }
}
