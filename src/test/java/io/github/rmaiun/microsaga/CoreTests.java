package io.github.rmaiun.microsaga;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.rmaiun.microsaga.exception.SagaActionFailedException;
import io.github.rmaiun.microsaga.exception.SagaCompensationFailedException;
import io.github.rmaiun.microsaga.component.SagaManager;
import io.github.rmaiun.microsaga.saga.Saga;
import io.github.rmaiun.microsaga.saga.SagaStep;
import io.github.rmaiun.microsaga.support.EvaluationResult;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;

public class CoreTests {

  @Test
  public void flatmapTest() {
    AtomicInteger x = new AtomicInteger();
    SagaStep<Integer> incrementStep = Sagas.action("initValue", x::incrementAndGet)
        .withoutCompensation();

    Saga<String> intToString = incrementStep.flatmap(a -> Sagas.action("intToString", () -> String.format("int=%d", a)).withoutCompensation());
    EvaluationResult<String> result = SagaManager.use(intToString).transact();
    assertNotNull(result);
    assertTrue(result.isSuccess());
    assertNotNull(result.getValue());
    assertEquals("int=1", result.getValue());
  }

  @Test
  public void mapFlatmapTest() {
    AtomicInteger x = new AtomicInteger();
    SagaStep<Integer> incrementStep = Sagas.action("initValue", x::incrementAndGet)
        .withoutCompensation();

    Saga<Integer> intToString = incrementStep
        .flatmap(a -> Sagas.action("intToString", () -> String.format("int=%d", a)).withoutCompensation())
        .map(str -> str.split("=").length);
    EvaluationResult<Integer> result = SagaManager.use(intToString).transact();
    assertNotNull(result);
    assertTrue(result.isSuccess());
    assertNotNull(result.getValue());
    assertEquals(2, result.getValue());
  }

  @Test
  public void compensatedFlatmapTest() {
    AtomicInteger x = new AtomicInteger();
    Callable<Integer> throwError = () -> {
      if (x.get() == 0) {
        throw new RuntimeException("Wrong value");
      } else {
        return x.incrementAndGet();
      }
    };

    SagaStep<Integer> incrementStep = Sagas.action("initValue", throwError)
        .compensate("setAtomicIntToZero", () -> x.set(0));

    Saga<Integer> intToString = incrementStep
        .flatmap(a -> Sagas.action("intToString", () -> String.format("int=%d", a)).withoutCompensation())
        .map(str -> str.split("=").length);
    EvaluationResult<Integer> result = SagaManager.use(intToString).transact();
    assertNotNull(result);
    assertFalse(result.isSuccess());
    assertEquals(SagaActionFailedException.class, result.getError().getClass());
  }

  @Test
  public void compensationFailedFlatmapTest() {
    AtomicInteger x = new AtomicInteger();
    Callable<Integer> throwError = () -> {
      if (x.get() == 0) {
        throw new RuntimeException("Wrong value");
      } else {
        return x.incrementAndGet();
      }
    };
    SagaStep<Integer> incrementStep = Sagas.action("initValue", throwError)
        .compensate("setAtomicIntToZero", () -> {
              throw new RuntimeException("something wrong");
            }
        );

    Saga<Integer> intToString = incrementStep
        .flatmap(a -> Sagas.action("intToString", () -> String.format("int=%d", a)).withoutCompensation())
        .map(str -> str.split("=").length);
    EvaluationResult<Integer> result = SagaManager.use(intToString).transact();
    assertNotNull(result);
    assertFalse(result.isSuccess());
    assertEquals(SagaCompensationFailedException.class, result.getError().getClass());
  }

  @Test
  public void thenTest() {
    AtomicInteger x = new AtomicInteger();
    SagaStep<Integer> incrementStep = Sagas.action("initValue", x::incrementAndGet)
        .withoutCompensation();

    Saga<String> intToString = incrementStep
        .then(Sagas.action("intToString", () -> String.format("int=%d", 1)).withoutCompensation())
        .then(Sagas.action("intToString", () -> String.format("int=%d", 2)).withoutCompensation())
        .then(Sagas.action("intToString", () -> String.format("int=%d", 3)).withoutCompensation());
    EvaluationResult<String> result = SagaManager.use(intToString).transact();
    assertNotNull(result);
    assertTrue(result.isSuccess());
    assertNotNull(result.getValue());
    assertEquals("int=3", result.getValue());
  }
}
