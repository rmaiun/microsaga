package dev.rmaiun.microsaga.component;


import dev.rmaiun.microsaga.saga.Saga;
import dev.rmaiun.microsaga.support.SagaRunner;
import org.apache.logging.log4j.Level;

public class SagaManager {

  private final Level loggingLvl;

  public SagaManager() {
    this.loggingLvl = Level.INFO;
  }

  public SagaManager(Level loggingLvl) {
    this.loggingLvl = loggingLvl;
  }

  public <A> SagaRunner<A> saga(Saga<A> saga) {
    SagaRunner<A> ctx = new SagaRunner<>();
    ctx.setSaga(saga);
    ctx.setLoggingLvl(loggingLvl);
    return ctx;
  }
}
