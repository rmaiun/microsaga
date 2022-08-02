package dev.rmaiun.microsaga.support;

import dev.rmaiun.microsaga.component.SagaTransactor;
import dev.rmaiun.microsaga.saga.Saga;
import java.util.UUID;
import java.util.function.Function;
import org.apache.logging.log4j.Level;

public class SagaRunner<A> {

  private String name = UUID.randomUUID().toString().replace("-", "");
  private Saga<A> saga;
  private Level loggingLvl;

  public EvaluationResult<A> transact() {
    return new SagaTransactor(loggingLvl).transact(name, saga);
  }

  public A transactOrThrow() {
    return new SagaTransactor(loggingLvl).transactOrThrow(name, saga);
  }

  public <E extends RuntimeException> A transactOrThrow(Saga<A> saga, Function<Throwable, E> errorTransformer) {
    return new SagaTransactor(loggingLvl).transactOrThrow(name, saga, errorTransformer);
  }


  public SagaRunner<A> withName(String name) {
    this.name = name;
    return this;
  }

  public SagaRunner<A> withLoggingLvl(Level lvl) {
    this.loggingLvl = lvl;
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

  public Level getLoggingLvl() {
    return loggingLvl;
  }

  public void setLoggingLvl(Level loggingLvl) {
    this.loggingLvl = loggingLvl;
  }
}
