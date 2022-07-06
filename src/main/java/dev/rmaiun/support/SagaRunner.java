package dev.rmaiun.support;

import dev.rmaiun.component.SagaPersistenceManager;
import dev.rmaiun.component.SagaTransactor;
import dev.rmaiun.saga.Saga;
import java.util.UUID;
import java.util.function.Function;

public class SagaRunner<A> {

  private String name = UUID.randomUUID().toString().replace("-", "");
  private Saga<A> saga;
  private SagaPersistenceManager spm;


  public A transact() {
    return new SagaTransactor().transact(name, saga);
  }

  public A transactOrThrow() {
    return new SagaTransactor().transactOrThrow(name, saga);
  }

  public <E extends RuntimeException> A transactOrThrow(Saga<A> saga, Function<Throwable, E> errorTransformer) {
    return new SagaTransactor().transactOrThrow(name, saga, errorTransformer);
  }


  public SagaRunner<A> withName(String name) {
    this.name = name;
    return this;
  }

  public SagaPersistenceManager getSpm() {
    return spm;
  }

  public void setSpm(SagaPersistenceManager spm) {
    this.spm = spm;
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

}
