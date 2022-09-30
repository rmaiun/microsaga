package io.github.simpleservice.helper;

import static io.github.rmaiun.microsaga.support.EvaluationType.COMPENSATION;
import static io.github.simpleservice.domain.SagaInstanceState.FAILED;
import static io.github.simpleservice.domain.SagaInstanceState.RETRY_PLANNED;
import static io.github.simpleservice.domain.SagaInstanceState.SUCCESS;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.rmaiun.microsaga.component.SagaManager;
import io.github.rmaiun.microsaga.exception.SagaActionFailedException;
import io.github.rmaiun.microsaga.exception.SagaCompensationFailedException;
import io.github.rmaiun.microsaga.support.EvaluationData;
import io.github.rmaiun.microsaga.support.EvaluationResult;
import io.github.rmaiun.microsaga.support.NoResult;
import io.github.simpleservice.domain.SagaInstance;
import io.github.simpleservice.domain.SagaInstanceState;
import io.github.simpleservice.domain.SagaInvocation;
import io.github.simpleservice.dto.BuyProductDto;
import io.github.simpleservice.repository.SagaInstanceRepository;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

@Service
public class BuyProductHelper {

  private static final Logger LOG = LogManager.getLogger(BuyProductHelper.class);
  private final OrderSagaHelper orderSagaHelper;
  private final PaymentSagaHelper paymentSagaHelper;
  private final DeliverySagaHelper deliverySagaHelper;
  private final SagaManager sagaManager;
  private final SagaInstanceRepository sagaRepository;
  private final ObjectMapper objectMapper;

  public BuyProductHelper(OrderSagaHelper orderSagaHelper, PaymentSagaHelper paymentSagaHelper, DeliverySagaHelper deliverySagaHelper,
      SagaManager sagaManager, SagaInstanceRepository sagaRepository, ObjectMapper objectMapper) {
    this.orderSagaHelper = orderSagaHelper;
    this.paymentSagaHelper = paymentSagaHelper;
    this.deliverySagaHelper = deliverySagaHelper;
    this.sagaManager = sagaManager;
    this.sagaRepository = sagaRepository;
    this.objectMapper = objectMapper;
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW, noRollbackFor = { SagaActionFailedException.class,
      SagaCompensationFailedException.class })
  public EvaluationResult<NoResult> buyProduct(BuyProductDto dto, List<SagaInvocation> invocationList) {
    SagaInstance sagaEntity = createSagaEntity(dto, invocationList);
    LOG.info("Starting saga {}", sagaEntity.getSagaId());
    var saga = orderSagaHelper.createOrderSagaStep(dto.client(), dto.product(), invocationList)
        .zipWith(x -> paymentSagaHelper.processPaymentSagaStep(x, invocationList), (in, out) -> Pair.of(out, dto.city()))
        .flatmap(p -> deliverySagaHelper.planDeliverySagaStep(p.getFirst(), p.getSecond(), invocationList));
    EvaluationResult<NoResult> sagaResult = sagaManager.saga(saga)
        .withId(sagaEntity.getSagaId())
        .transact()
        .peek(er -> SagaLogger.logSaga(er.getEvaluationHistory()))
        .peek(this::logEvaluation);
    LOG.info("Updating saga {}", sagaEntity.getSagaId());
    updateSaga(sagaEntity, sagaResult);
    return sagaResult;
  }

  private void logEvaluation(EvaluationResult<NoResult> er) {
    if (er.isError()) {
      LOG.error("Couldn't buy some product", er.getError());
    } else {
      LOG.info("Saga {} is successfully finished", er.getEvaluationHistory().getSagaId());
    }
  }

  private void updateSaga(SagaInstance sagaInstance, EvaluationResult<NoResult> evaluationResult) {
    Set<SagaInvocation> sagaExecutions = evaluationResult.getEvaluationHistory().getEvaluations().stream()
        .map(e -> new SagaInvocation(sagaInstance.getSagaId(), e.getName(), e.isSuccess(),
            COMPENSATION == e.getEvaluationType(), objectToString(e.getResult())))
        .collect(Collectors.toSet());
    ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
    if (evaluationResult.isError()
        && (RETRY_PLANNED == sagaInstance.getState() || evaluationResult.getError() instanceof SagaActionFailedException)) {
      sagaInstance.setState(FAILED);
    } else if (evaluationResult.isError() && evaluationResult.getError() instanceof SagaCompensationFailedException) {
      sagaInstance.setState(RETRY_PLANNED);
      sagaInstance.setRetryAfter(now.plusSeconds(5));
    } else {
      sagaInstance.setState(SUCCESS);
    }
    sagaInstance.setFinishedAt(now);
    sagaInstance.setInvocations(sagaExecutions);
    sagaRepository.save(sagaInstance);
  }

  private SagaInstance createSagaEntity(BuyProductDto dto, List<SagaInvocation> invocationList) {
    if (CollectionUtils.isEmpty(invocationList)) {
      SagaInstance sagaInstance = new SagaInstance();
      String sagaId = String.format("BUYPROD|%s", UUID.randomUUID().toString().replace("-", ""));
      sagaInstance.setSagaId(sagaId);
      sagaInstance.setState(SagaInstanceState.SUBMITTED);
      sagaInstance.setInput(objectToString(new EvaluationData<>(dto, dto.getClass().getName())));
      return sagaRepository.save(sagaInstance);
    } else {
      String sagaId = invocationList.get(0).getSagaId();
      var found = sagaRepository.findSagaInstanceBySagaId(sagaId)
          .orElseThrow(() -> new RuntimeException(String.format("Saga with id %s is not found", sagaId)));
      found.setInvocations(new HashSet<>(invocationList));
      return found;
    }
  }

  private <T> String objectToString(T dto) {
    try {
      return objectMapper.writeValueAsString(dto);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }
}
