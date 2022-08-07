package com.rmaiun.microsaga.core.support;

import com.rmaiun.microsaga.core.component.DefaultSagaTransactor;
import com.rmaiun.microsaga.core.component.SagaTransactor;
import com.rmaiun.microsaga.core.saga.Saga;
import java.util.UUID;
import java.util.function.Function;

public class SagaRunner<A> {

  private final SagaTransactor sagaTransactor;
  private String name = UUID.randomUUID().toString().replace("-", "");
  private Saga<A> saga;

  public SagaRunner(SagaTransactor sagaTransactor) {
    this.sagaTransactor = sagaTransactor;
  }

  public EvaluationResult<A> transact() {
    return new DefaultSagaTransactor().transact(name, saga);
  }

  public A transactOrThrow() {
    return new DefaultSagaTransactor().transactOrThrow(name, saga);
  }

  public <E extends RuntimeException> A transactOrThrow(Saga<A> saga, Function<Throwable, E> errorTransformer) {
    return new DefaultSagaTransactor().transactOrThrow(name, saga, errorTransformer);
  }


  public SagaRunner<A> withName(String name) {
    this.name = name;
    return this;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
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
