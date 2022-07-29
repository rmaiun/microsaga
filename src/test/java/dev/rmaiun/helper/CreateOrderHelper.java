package dev.rmaiun.helper;

import dev.rmaiun.dto.CreateOrderDto;
import dev.rmaiun.dto.PaymentRequest;
import dev.rmaiun.dto.ProductOrder;
import dev.rmaiun.saga4j.Sagas;
import dev.rmaiun.saga4j.component.SagaManager;
import dev.rmaiun.saga4j.saga.SagaStep;
import dev.rmaiun.services.DeliveryService;
import dev.rmaiun.services.MoneyTransferService;
import dev.rmaiun.services.OrderService;

public class CreateOrderHelper {

  private final OrderService orderService;
  private final MoneyTransferService moneyTransferService;
  private final DeliveryService deliveryService;

  public CreateOrderHelper(OrderService orderService, MoneyTransferService moneyTransferService, DeliveryService deliveryService) {
    this.orderService = orderService;
    this.moneyTransferService = moneyTransferService;
    this.deliveryService = deliveryService;
  }

  public void createOrder(CreateOrderDto dto) {
    SagaStep<ProductOrder> orderSagaPart = Sagas.action("makeOrder", () -> orderService.makeOrder(dto.getProduct()))
        .compensate(Sagas.compensation("cancelOrder", () -> orderService.cancelOrder(dto.getProduct())));

    SagaStep<Boolean> paymentSagaPart = Sagas.action("processPayment", () -> moneyTransferService.processPayment(new PaymentRequest(dto.getPerson(), 100)))
        .compensate(Sagas.compensation("cancelPayment", () -> moneyTransferService.processPayment(new PaymentRequest(dto.getPerson(), -100))));

    SagaStep<Boolean> deliverySagaPart = Sagas.action("registerDelivery", () -> deliveryService.registerDelivery(dto.getPerson()))
        .withoutCompensation();

    Boolean result = new SagaManager()
        .saga(orderSagaPart.then(paymentSagaPart).then(deliverySagaPart))
        .withName("testSaga")
        .transactOrThrow();
  }
}
