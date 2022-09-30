package io.github.simpleservice.helper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.rmaiun.microsaga.support.EvaluationData;
import io.github.simpleservice.domain.SagaInstanceState;
import io.github.simpleservice.dto.BuyProductDto;
import io.github.simpleservice.repository.SagaInstanceRepository;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import javax.annotation.PostConstruct;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SagaRecoveryHelper {

  private static final Logger LOG = LogManager.getLogger(SagaRecoveryHelper.class);
  private final BuyProductHelper buyProductHelper;
  private final SagaInstanceRepository sagaInstanceRepository;
  private final ObjectMapper objectMapper;

  public SagaRecoveryHelper(BuyProductHelper buyProductHelper, SagaInstanceRepository sagaInstanceRepository, ObjectMapper objectMapper) {
    this.buyProductHelper = buyProductHelper;
    this.sagaInstanceRepository = sagaInstanceRepository;
    this.objectMapper = objectMapper;
  }

  @Scheduled(cron = "*/10 * * * * *")
  public void runSagaRecovery() {
    var sagaInstances = sagaInstanceRepository.findAllByStateAndRetryAfterBefore(SagaInstanceState.RETRY_PLANNED,
        ZonedDateTime.now(ZoneOffset.UTC));
    if (sagaInstances.size() > 0) {
      sagaInstances.forEach(saga -> {
        var dto = strToObject(saga.getInput());
        var invocations = sagaInstanceRepository.findSagaInvocations(saga.getSagaId());
        var result = buyProductHelper.buyProduct(dto, invocations);
        var msg = result.fold(
            res -> String.format("Saga %s is successfully recovered", saga.getSagaId()),
            err -> String.format("Saga %s recovery is failed with error '%s'", saga.getSagaId(), result.getError().getMessage()));
        LOG.info(msg);
      });
    }
  }

  private BuyProductDto strToObject(String result) {
    try {
      var evaluationData = objectMapper.readValue(result, new TypeReference<EvaluationData<BuyProductDto>>() {
      });
      return evaluationData.getData();
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

}
