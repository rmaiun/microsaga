package dev.rmaiun;

import java.util.function.Supplier;

public interface SagaAction<T> {

  T action();

}
