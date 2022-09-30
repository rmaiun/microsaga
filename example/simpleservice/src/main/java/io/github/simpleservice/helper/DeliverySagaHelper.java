package io.github.simpleservice.helper;

import io.github.rmaiun.microsaga.saga.SagaStep;
import io.github.rmaiun.microsaga.support.NoResult;
import io.github.simpleservice.domain.SagaInvocation;
import io.github.simpleservice.dto.PaymentProcessedDto;
import io.github.simpleservice.dto.PlanDeliveryDto;
import io.github.simpleservice.service.DeliveryService;
import java.util.List;
import net.jodah.failsafe.RetryPolicy;
import org.springframework.stereotype.Service;

@Service
public class DeliverySagaHelper {

  private final DeliveryService deliveryService;
  private final SagaRequestHelper sagaRequestHelper;

  public DeliverySagaHelper(DeliveryService deliveryService, SagaRequestHelper sagaRequestHelper) {
    this.deliveryService = deliveryService;
    this.sagaRequestHelper = sagaRequestHelper;
  }

  public SagaStep<NoResult> planDeliverySagaStep(PaymentProcessedDto dto, String city, List<SagaInvocation> invocationList) {
    var retryPolicy = new RetryPolicy<NoResult>().withMaxRetries(3);
    return sagaRequestHelper
        .mkAction("planDelivery", () -> deliveryService.planDelivery(new PlanDeliveryDto(dto.payer(), city)), invocationList, retryPolicy)
        .withoutCompensation();
  }
}
