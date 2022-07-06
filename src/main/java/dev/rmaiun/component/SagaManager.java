package dev.rmaiun.component;

import dev.rmaiun.support.SagaRunner;
import dev.rmaiun.saga.Saga;

public class SagaManager {

  private SagaPersistenceManager sagaPersistenceManager = new SagaPersistenceManager();

  public <A> SagaRunner<A> saga(Saga<A> saga) {
    SagaRunner<A> ctx = new SagaRunner<>();
    ctx.setSaga(saga);
    ctx.setSpm(sagaPersistenceManager);
    return ctx;
  }
}
