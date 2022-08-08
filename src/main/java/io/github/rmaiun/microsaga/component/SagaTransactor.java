package io.github.rmaiun.microsaga.component;

import io.github.rmaiun.microsaga.saga.Saga;
import io.github.rmaiun.microsaga.support.EvaluationResult;
import java.util.function.Function;

public interface SagaTransactor {

  <A> EvaluationResult<A> transact(String sagaName, Saga<A> saga);

  <A> A transactOrThrow(String sagaName, Saga<A> saga);

  <A, E extends RuntimeException> A transactOrThrow(String sagaName, Saga<A> saga, Function<Throwable, E> errorTransformer);

}
