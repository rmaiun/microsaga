package io.github.rmaiun.microsaga.component;

import io.github.rmaiun.microsaga.saga.Saga;
import io.github.rmaiun.microsaga.support.EvaluationResult;
import io.github.rmaiun.microsaga.util.SagaUtils;

public class SagaRunner<A> {

  private final SagaTransactor sagaTransactor;
  private String id;
  private Saga<A> saga;

  public SagaRunner(SagaTransactor sagaTransactor) {
    this.sagaTransactor = sagaTransactor;
    this.id = SagaUtils.defaultId();
  }

  public EvaluationResult<A> transact() {
    return sagaTransactor.transact(id, saga);
  }

  public SagaRunner<A> withId(String id) {
    this.id = id;
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
