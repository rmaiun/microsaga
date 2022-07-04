package dev.rmaiun;

import java.util.UUID;

public class SagaTransactionCtx<A> {

  private String name = UUID.randomUUID().toString().replace("-", "");
  private Saga<A> saga;
  private SagaPersistenceManager spm;


  public A transact() {
    return new SagaTransactor().transact(saga);
  }


  public SagaTransactionCtx<A> withName(String name) {
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
