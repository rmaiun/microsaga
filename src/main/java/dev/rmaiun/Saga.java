package dev.rmaiun;

public class Saga<A> implements SagaAction<A>, SagaCompensation {

  private SagaAction<A> sagaAction;
  private SagaCompensation compensation;

  public SagaAction<A> getSagaAction() {
    return sagaAction;
  }

  public void setSagaAction(SagaAction<A> sagaAction) {
    this.sagaAction = sagaAction;
  }

  public SagaCompensation getCompensation() {
    return compensation;
  }

  public void setCompensation(SagaCompensation compensation) {
    this.compensation = compensation;
  }

  public static <A> Saga<A> action(SagaAction<A> action) {
    Saga<A> saga = new Saga<>();
    saga.setSagaAction(action);
    return saga;
  }

  public Saga<A> compensate(SagaCompensation compensationAction) {
    Saga<A> saga = new Saga<>();
    saga.setSagaAction(this.getSagaAction());
    saga.setCompensation(compensationAction);
    return saga;
  }

  @Override
  public A action() {
    return sagaAction.action();
  }

  @Override
  public void compensate() {
    compensation.compensate();
  }
}
