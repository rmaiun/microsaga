package com.rmaiun.microsaga.core.component;

import com.rmaiun.microsaga.core.saga.Saga;
import com.rmaiun.microsaga.core.support.EvaluationResult;
import java.util.function.Function;

public interface SagaTransactor {

  <A> EvaluationResult<A> transact(String sagaName, Saga<A> saga);

  <A> A transactOrThrow(String sagaName, Saga<A> saga);

  <A, E extends RuntimeException> A transactOrThrow(String sagaName, Saga<A> saga, Function<Throwable, E> errorTransformer);

}
