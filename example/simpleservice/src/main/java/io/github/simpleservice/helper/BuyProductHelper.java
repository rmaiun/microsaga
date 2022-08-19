package io.github.simpleservice.helper;

import io.github.rmaiun.microsaga.component.SagaManager;
import org.springframework.stereotype.Service;

@Service
public class BuyProductHelper {

  private final OrderSagaHelper orderSagaHelper;
  private final PaymentSagaHelper paymentSagaHelper;
  private final DeliverySagaHelper deliverySagaHelper;
  private final SagaManager sagaManager;

  public BuyProductHelper(OrderSagaHelper orderSagaHelper, PaymentSagaHelper paymentSagaHelper, DeliverySagaHelper deliverySagaHelper, SagaManager sagaManager) {
    this.orderSagaHelper = orderSagaHelper;
    this.paymentSagaHelper = paymentSagaHelper;
    this.deliverySagaHelper = deliverySagaHelper;
    this.sagaManager = sagaManager;
  }

  public void buyProduct(String client, String product, String city) {
    var sagaId = String.format("%s:%s-%d", "BuyProductSaga", Thread.currentThread().getName(), Thread.currentThread().getId());
    var saga = orderSagaHelper.createOrderSagaStep(client, sagaId, product)
        .flatmap(orderCreatedDto -> paymentSagaHelper.processPaymentSagaStep(sagaId, orderCreatedDto, city))
        .flatmap(deliverySagaHelper::planDeliverySagaStep);
    sagaManager.saga(saga).withName(sagaId).transactOrThrow();
  }
}
