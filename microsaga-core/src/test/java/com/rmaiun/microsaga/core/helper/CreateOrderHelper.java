package com.rmaiun.microsaga.core.helper;

import com.rmaiun.microsaga.core.Sagas;
import com.rmaiun.microsaga.core.component.SagaManager;
import com.rmaiun.microsaga.core.dto.CreateOrderDto;
import com.rmaiun.microsaga.core.dto.PaymentRequest;
import com.rmaiun.microsaga.core.dto.ProductOrder;
import com.rmaiun.microsaga.core.saga.SagaStep;
import com.rmaiun.microsaga.core.services.BusinessLogger;
import com.rmaiun.microsaga.core.services.DeliveryService;
import com.rmaiun.microsaga.core.services.MoneyTransferService;
import com.rmaiun.microsaga.core.services.OrderService;
import com.rmaiun.microsaga.core.support.Evaluation;
import com.rmaiun.microsaga.core.support.EvaluationHistory;
import com.rmaiun.microsaga.core.support.EvaluationResult;
import com.rmaiun.microsaga.core.support.NoResult;
import net.jodah.failsafe.RetryPolicy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CreateOrderHelper {

  private static final Logger LOG = LogManager.getLogger(CreateOrderHelper.class);
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

    new SagaManager()
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
    logSaga(result.getEvaluationHistory());
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
    logSaga(result.getEvaluationHistory());
  }

  public void createOrdersWithRetryAction(CreateOrderDto dto) {
    SagaStep<ProductOrder> orderSagaPart = Sagas.action("makeOrder", () -> orderService.makeOrder(dto.getProduct()))
        .compensate(Sagas.compensation("cancelOrder", () -> orderService.cancelOrder(dto.getProduct())));

    SagaStep<NoResult> paymentSagaPart = Sagas.retryableAction("processLaggingPayment", () -> moneyTransferService.processLaggingPayment(new PaymentRequest(dto.getPerson(), 100)),
            new RetryPolicy<NoResult>().withMaxRetries(4))
        .compensate(Sagas.compensation("cancelPayment (lagging)", () -> moneyTransferService.processPayment(new PaymentRequest(dto.getPerson(), -100))));

    SagaStep<NoResult> deliverySagaPart = Sagas.action("registerDelivery", () -> deliveryService.registerDelivery(dto.getPerson()))
        .compensate(Sagas.compensation("failedDeliveryBusinessLog", () -> businessLogger.createBusinessLog("Delivery planning was failed for user " + dto.getPerson())));

    EvaluationResult<NoResult> result = new SagaManager()
        .saga(orderSagaPart.then(paymentSagaPart).then(deliverySagaPart))
        .withName("testSaga")
        .transact();
    logSaga(result.getEvaluationHistory());
  }

  private void logSaga(EvaluationHistory evaluationHistory) {
    for (Evaluation e : evaluationHistory.getEvaluations()) {
      LOG.info("SAGA:{} [{}:{}] {} {}(ms)", evaluationHistory.getSagaName(), e.getEvaluationType().name().toLowerCase(), e.isSuccess() ? "success" : "failed", e.getName(), e.getDuration());
    }
  }
}