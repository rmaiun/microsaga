package dev.rmaiun.helper;

import dev.rmaiun.dto.CreateOrderDto;
import dev.rmaiun.dto.PaymentRequest;
import dev.rmaiun.dto.ProductOrder;
import dev.rmaiun.saga4j.Sagas;
import dev.rmaiun.saga4j.component.SagaManager;
import dev.rmaiun.saga4j.saga.SagaStep;
import dev.rmaiun.saga4j.support.EvaluationResult;
import dev.rmaiun.saga4j.support.NoResult;
import dev.rmaiun.services.BusinessLogger;
import dev.rmaiun.services.DeliveryService;
import dev.rmaiun.services.MoneyTransferService;
import dev.rmaiun.services.OrderService;
import net.jodah.failsafe.RetryPolicy;

public class CreateOrderHelper {

  private final OrderService orderService;
  private final MoneyTransferService moneyTransferService;
  private final DeliveryService deliveryService;
  private final BusinessLogger businessLogger;

  public CreateOrderHelper(OrderService orderService, MoneyTransferService moneyTransferService, DeliveryService deliveryService, BusinessLogger businessLogger) {
    this.orderService = orderService;
    this.moneyTransferService = moneyTransferService;
    this.deliveryService = deliveryService;
    this.businessLogger = businessLogger;
  }

  public void createOrder(CreateOrderDto dto) {
    SagaStep<ProductOrder> orderSagaPart = Sagas.action("makeOrder", () -> orderService.makeOrder(dto.getProduct()))
        .compensate(Sagas.compensation("cancelOrder", () -> orderService.cancelOrder(dto.getProduct())));

    SagaStep<NoResult> paymentSagaPart = Sagas.action("processPayment", () -> moneyTransferService.processPayment(new PaymentRequest(dto.getPerson(), 100)))
        .compensate(Sagas.compensation("cancelPayment", () -> moneyTransferService.processPayment(new PaymentRequest(dto.getPerson(), -100))));

    SagaStep<NoResult> deliverySagaPart = Sagas.action("registerDelivery", () -> deliveryService.registerDelivery(dto.getPerson()))
        .withoutCompensation();

    NoResult result = new SagaManager()
        .saga(orderSagaPart.then(paymentSagaPart).then(deliverySagaPart))
        .withName("testSaga")
        .transactOrThrow();
  }

  public void createOrdersWithFailedDelivery(CreateOrderDto dto) {
    SagaStep<ProductOrder> orderSagaPart = Sagas.action("makeOrder", () -> orderService.makeOrder(dto.getProduct()))
        .compensate(Sagas.compensation("cancelOrder", () -> orderService.cancelOrder(dto.getProduct())));

    SagaStep<NoResult> paymentSagaPart = Sagas.action("processPayment", () -> moneyTransferService.processPayment(new PaymentRequest(dto.getPerson(), 100)))
        .compensate(Sagas.compensation("cancelPayment", () -> moneyTransferService.processPayment(new PaymentRequest(dto.getPerson(), -100))));

    SagaStep<NoResult> deliverySagaPart = Sagas.action("registerDelivery", () -> deliveryService.registerDeliveryWithWrongAddress(dto.getPerson()))
        .compensate(Sagas.compensation("failedDeliveryBusinessLog", () -> businessLogger.createBusinessLog("Delivery planning was failed for user " + dto.getPerson())));

    EvaluationResult<NoResult> result = new SagaManager()
        .saga(orderSagaPart.then(paymentSagaPart).then(deliverySagaPart))
        .withName("testSaga")
        .transact();
  }

  public void createOrdersWithRetryCompensation(CreateOrderDto dto) {
    SagaStep<ProductOrder> orderSagaPart = Sagas.action("makeOrder", () -> orderService.makeOrder(dto.getProduct()))
        .compensate(Sagas.compensation("cancelOrder", () -> orderService.cancelOrder(dto.getProduct())));

    SagaStep<NoResult> paymentSagaPart = Sagas.action("processPayment", () -> moneyTransferService.processPayment(new PaymentRequest(dto.getPerson(), 100)))
        .compensate(Sagas.retryableCompensation("cancelPayment (lagging)", () -> moneyTransferService.processLaggingPayment(new PaymentRequest(dto.getPerson(), -100)),
            new RetryPolicy<>().withMaxRetries(4)));

    SagaStep<NoResult> deliverySagaPart = Sagas.action("registerDelivery", () -> deliveryService.registerDeliveryWithWrongAddress(dto.getPerson()))
        .compensate(Sagas.compensation("failedDeliveryBusinessLog", () -> businessLogger.createBusinessLog("Delivery planning was failed for user " + dto.getPerson())));

    EvaluationResult<NoResult> result = new SagaManager()
        .saga(orderSagaPart.then(paymentSagaPart).then(deliverySagaPart))
        .withName("testSaga")
        .transact();
  }

  public void createOrdersWithRetryAction(CreateOrderDto dto) {
    SagaStep<ProductOrder> orderSagaPart = Sagas.action("makeOrder", () -> orderService.makeOrder(dto.getProduct()))
        .compensate(Sagas.compensation("cancelOrder", () -> orderService.cancelOrder(dto.getProduct())));

    SagaStep<NoResult> paymentSagaPart = Sagas.retryableAction("processLaggingPayment", () -> moneyTransferService.processLaggingPayment(new PaymentRequest(dto.getPerson(), 100)),
            new RetryPolicy<NoResult>().withMaxRetries(4))
        .compensate(Sagas.compensation("cancelPayment (lagging)", () -> moneyTransferService.processPayment(new PaymentRequest(dto.getPerson(), -100))));

    SagaStep<NoResult> deliverySagaPart = Sagas.action("registerDelivery", () -> deliveryService.registerDeliveryWithWrongAddress(dto.getPerson()))
        .compensate(Sagas.compensation("failedDeliveryBusinessLog", () -> businessLogger.createBusinessLog("Delivery planning was failed for user " + dto.getPerson())));

    EvaluationResult<NoResult> result = new SagaManager()
        .saga(orderSagaPart.then(paymentSagaPart).then(deliverySagaPart))
        .withName("testSaga")
        .transact();
  }
}
