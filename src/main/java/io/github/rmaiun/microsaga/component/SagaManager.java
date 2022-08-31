package io.github.rmaiun.microsaga.component;

import io.github.rmaiun.microsaga.saga.Saga;
import io.github.rmaiun.microsaga.support.SagaRunner;

public class SagaManager {

  private final SagaTransactor sagaTransactor;

  public SagaManager() {
    this.sagaTransactor = new DefaultSagaTransactor();
  }

  public SagaManager(SagaTransactor sagaTransactor) {
    this.sagaTransactor = sagaTransactor;
  }

  public static <A> SagaRunner<A> use(Saga<A> saga, SagaTransactor sagaTransactor) {
    SagaRunner<A> sagaRunner = new SagaRunner<>(sagaTransactor);
    sagaRunner.setSaga(saga);
    return sagaRunner;
  }

  public static <A> SagaRunner<A> use(Saga<A> saga) {
    SagaRunner<A> sagaRunner = new SagaRunner<>(new DefaultSagaTransactor());
    sagaRunner.setSaga(saga);
    return sagaRunner;
  }

  public <A> SagaRunner<A> saga(Saga<A> saga) {
    SagaRunner<A> ctx = new SagaRunner<>(sagaTransactor);
    ctx.setSaga(saga);
    return ctx;
  }
}
