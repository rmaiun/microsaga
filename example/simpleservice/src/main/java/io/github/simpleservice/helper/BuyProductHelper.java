package io.github.simpleservice.helper;

import io.github.rmaiun.microsaga.component.SagaManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

@Service
public class BuyProductHelper {

  private static final Logger LOG = LogManager.getLogger(BuyProductHelper.class);
  private final OrderSagaHelper orderSagaHelper;
  private final PaymentSagaHelper paymentSagaHelper;
  private final DeliverySagaHelper deliverySagaHelper;
  private final SagaManager sagaManager;

  public BuyProductHelper(OrderSagaHelper orderSagaHelper, PaymentSagaHelper paymentSagaHelper, DeliverySagaHelper deliverySagaHelper,
      SagaManager sagaManager) {
    this.orderSagaHelper = orderSagaHelper;
    this.paymentSagaHelper = paymentSagaHelper;
    this.deliverySagaHelper = deliverySagaHelper;
    this.sagaManager = sagaManager;
  }

  public void buyProduct(String client, String product, String city) {
    var saga = orderSagaHelper.createOrderSagaStep(client, product)
        .zipWith(paymentSagaHelper::processPaymentSagaStep, (in, out) -> Pair.of(out, city))
        .flatmap(p -> deliverySagaHelper.planDeliverySagaStep(p.getFirst(), p.getSecond()));
    sagaManager.saga(saga)
        .transact()
        .peek(er -> SagaLogger.logSaga(er.getEvaluationHistory()))
        .peek(er -> {
          if (er.isError()) {
            LOG.error("Couldn't buy some product", er.getError());
            er.getError().printStackTrace();
          } else {
            LOG.info("Saga {} is successfully finished", er.getEvaluationHistory().getSagaId());
          }
        }).valueOrThrow();
  }
}
