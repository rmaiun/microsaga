package io.github.simpleservice.helper;

import io.github.rmaiun.microsaga.Sagas;
import io.github.rmaiun.microsaga.saga.SagaStep;
import io.github.simpleservice.dto.OrderCreatedDto;
import io.github.simpleservice.dto.PaymentProcessedDto;
import io.github.simpleservice.dto.ProcessPaymentDto;
import io.github.simpleservice.service.PaymentService;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import net.jodah.failsafe.RetryPolicy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PaymentSagaHelper {

  private final PaymentService paymentService;

  @Value("${company.name}")
  private String companyName;

  public PaymentSagaHelper(PaymentService paymentService) {
    this.paymentService = paymentService;
  }

  public SagaStep<PaymentProcessedDto> processPaymentSagaStep(String sagaId, OrderCreatedDto dto, String city) {
    var action = Sagas.action("processPayment",
        () -> paymentService.processPayment(new ProcessPaymentDto(dto.client(), companyName, dto.price(), dto.id(), sagaId), city));
    var compensation = Sagas.retryableCompensation("cancelPayment",
        () -> paymentService.cancelPayment(sagaId), new RetryPolicy<>().withDelay(Duration.of(5L, ChronoUnit.SECONDS)));
    return action.compensate(compensation);
  }
}
