package io.github.rmaiun.microsaga.component;

import io.github.rmaiun.microsaga.saga.Saga;
import io.github.rmaiun.microsaga.support.EvaluationResult;

public interface SagaTransactor {

  <A> EvaluationResult<A> transact(String sagaName, Saga<A> saga);
}
