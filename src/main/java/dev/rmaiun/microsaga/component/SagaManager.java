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

  public static <A> SagaRunner<A> use(Saga<A> saga){
    SagaRunner<A> sagaRunner = new SagaRunner<>();
    sagaRunner.setSaga(saga);
    return sagaRunner;
  }
  public <A> SagaRunner<A> saga(Saga<A> saga) {
    SagaRunner<A> ctx = new SagaRunner<>();
    ctx.setSaga(saga);
    ctx.setLoggingLvl(loggingLvl);
    return ctx;
  }
}
