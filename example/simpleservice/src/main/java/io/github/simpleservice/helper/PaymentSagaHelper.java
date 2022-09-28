package io.github.simpleservice.helper;

import io.github.rmaiun.microsaga.saga.SagaStep;
import io.github.simpleservice.domain.SagaInvocation;
import io.github.simpleservice.dto.OrderCreatedDto;
import io.github.simpleservice.dto.PaymentProcessedDto;
import io.github.simpleservice.dto.ProcessPaymentDto;
import io.github.simpleservice.service.PaymentService;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;
import net.jodah.failsafe.RetryPolicy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PaymentSagaHelper {

  private final PaymentService paymentService;
  private final SagaRequestHelper sagaRequestHelper;


  @Value("${company.name}")
  private String companyName;

  public PaymentSagaHelper(PaymentService paymentService, SagaRequestHelper sagaRequestHelper) {
    this.paymentService = paymentService;
    this.sagaRequestHelper = sagaRequestHelper;
  }

  public SagaStep<PaymentProcessedDto> processPaymentSagaStep(OrderCreatedDto dto, List<SagaInvocation> invocationList) {
    var retryPolicy = new RetryPolicy<>().withDelay(Duration.of(5L, ChronoUnit.SECONDS));
    var action = sagaRequestHelper.mkAction("processPayment",
        sagaId -> paymentService.processPayment(new ProcessPaymentDto(dto.client(), companyName, dto.price(), dto.id(), sagaId)), invocationList);
    var compensation = sagaRequestHelper.mkCompensation("cancelPayment",
        paymentService::cancelPayment, invocationList, retryPolicy);
    return action.compensate(compensation);
  }
}
