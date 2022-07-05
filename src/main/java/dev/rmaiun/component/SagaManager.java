package dev.rmaiun.component;

import dev.rmaiun.support.SagaTransactionCtx;
import dev.rmaiun.saga.Saga;

public class SagaManager {

  private SagaPersistenceManager sagaPersistenceManager = new SagaPersistenceManager();

  public <A> SagaTransactionCtx<A> saga(Saga<A> saga) {
    SagaTransactionCtx<A> ctx = new SagaTransactionCtx<>();
    ctx.setSaga(saga);
    ctx.setSpm(sagaPersistenceManager);
    return ctx;
  }
}
