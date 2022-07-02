package dev.rmaiun;

public class Sagas {

  private Sagas() {
  }

  public static <T> SagaStep<T,?> saga(Saga<T> sagaAction) {
    return new SagaStep<>(sagaAction, null);
  }
}
