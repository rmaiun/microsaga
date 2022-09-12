package io.github.simpleservice.helper;

import io.github.rmaiun.microsaga.support.EvaluationHistory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SagaLogger {

  private static final Logger LOG = LogManager.getLogger(SagaLogger.class);

  private SagaLogger() {
  }

  public static void logSaga(EvaluationHistory eh) {
    eh.getEvaluations().forEach(e -> LOG.info("SAGA:{}:[{}] {} ({}) {}", eh.getSagaId(),
        e.getEvaluationType().name().toLowerCase(), e.getName(), e.getDuration(), e.isSuccess() ? "✅" : "❌"));
  }
}
