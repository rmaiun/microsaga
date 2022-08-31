package io.github.rmaiun.microsaga.support;

import io.github.rmaiun.microsaga.component.SagaTransactor;
import io.github.rmaiun.microsaga.saga.Saga;
import java.util.UUID;
import java.util.function.Function;

public class SagaRunner<A> {

  private final SagaTransactor sagaTransactor;
  private String id = UUID.randomUUID().toString().replace("-", "");
  private Saga<A> saga;

  public SagaRunner(SagaTransactor sagaTransactor) {
    this.sagaTransactor = sagaTransactor;
  }

  public EvaluationResult<A> transact() {
    return sagaTransactor.transact(id, saga);
  }

  public A transactOrThrow() {
    return sagaTransactor.transactOrThrow(id, saga);
  }

  public <E extends RuntimeException> A transactOrThrow(Saga<A> saga, Function<Throwable, E> errorTransformer) {
    return sagaTransactor.transactOrThrow(id, saga, errorTransformer);
  }

  public SagaRunner<A> withName(String name) {
    this.id = name;
    return this;
  }

  public String getId() {
    return id;
  }

  public void setId(String name) {
    this.id = name;
  }

  public Saga<A> getSaga() {
    return saga;
  }

  public void setSaga(Saga<A> saga) {
    this.saga = saga;
  }

  public SagaTransactor getSagaTransactor() {
    return sagaTransactor;
  }
}
