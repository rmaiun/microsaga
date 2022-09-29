package io.github.rmaiun.microsaga;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.rmaiun.microsaga.component.SagaManager;
import io.github.rmaiun.microsaga.exception.SagaActionFailedException;
import io.github.rmaiun.microsaga.exception.SagaCompensationFailedException;
import io.github.rmaiun.microsaga.saga.Saga;
import io.github.rmaiun.microsaga.saga.SagaStep;
import io.github.rmaiun.microsaga.support.EvaluationResult;
import io.github.rmaiun.microsaga.support.NoResult;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import net.jodah.failsafe.RetryPolicy;
import org.junit.jupiter.api.Test;

public class CoreTest {

  @Test
  public void flatmapTest() {
    AtomicInteger x = new AtomicInteger();
    SagaStep<Integer> incrementStep = Sagas.action("initValue", x::incrementAndGet)
        .withoutCompensation();

    Saga<String> intToString = incrementStep
        .flatmap(a -> Sagas.action("intToString", () -> String.format("int=%d", a)).withoutCompensation());
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
        });

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

  @Test
  public void zipWithTest1() {
    AtomicInteger x = new AtomicInteger();
    SagaStep<Integer> incrementStep = Sagas.action("initValue", x::incrementAndGet)
        .withoutCompensation();
    Saga<Integer> intToString = incrementStep
        .zipWith(a -> Sagas.action("intToString", () -> String.format("int=%d", a)).withoutCompensation(), (a, b) -> a)
        .flatmap(a -> Sagas.action("+3", () -> a + 3).withoutCompensation());
    Integer result = SagaManager.use(intToString).transact().valueOrThrow();
    assertNotNull(result);
    assertEquals(4, result);
  }

  @Test
  public void zipWithTest2() {
    AtomicInteger x = new AtomicInteger();
    SagaStep<Integer> incrementStep = Sagas.action("initValue", x::incrementAndGet)
        .withoutCompensation();
    Saga<String> intToString = incrementStep
        .zipWith(Sagas.action("intToString", () -> String.format("int=%d", 14)).withoutCompensation(), a -> a + 15)
        .flatmap(a -> Sagas.action("+3", () -> a + 3).withoutCompensation());
    String result = SagaManager.use(intToString).transact().valueOrThrow();
    assertNotNull(result);
    assertEquals("int=14153", result);
  }

  @Test
  public void evaluationResultFlatTapTest() {
    List<Integer> list = new ArrayList<>();
    AtomicInteger x = new AtomicInteger();
    SagaStep<Integer> incrementStep = Sagas.action("initValue", x::incrementAndGet)
        .withoutCompensation();
    Saga<String> intToString = incrementStep
        .zipWith(Sagas.action("intToString", () -> String.format("int=%d", 14)).withoutCompensation(), a -> a + 15)
        .flatmap(a -> Sagas.action("+3", () -> a + 3).withoutCompensation());
    String result = SagaManager.use(intToString).transact()
        .peek(er -> list.add(er.getEvaluationHistory().getEvaluations().size()))
        .valueOrThrow();
    assertNotNull(result);
    assertEquals("int=14153", result);
    assertNotNull(list);
    assertNotNull(list.get(0));
    assertEquals(3, list.get(0));
  }

  @Test
  public void voidActionTest() {
    AtomicReference<String> ref = new AtomicReference<>();
    SagaStep<NoResult> saga = Sagas.voidAction("action#1", () -> {
      throw new RuntimeException("action#1 failed");
    })
        .compensate("compensation#1", ref::set);
    EvaluationResult<NoResult> evaluationResult = SagaManager.use(saga).transact();
    assertNotNull(evaluationResult);
    assertEquals(evaluationResult.getEvaluationHistory().getSagaId(), ref.get());
  }

  @Test
  public void voidRetriableActionTest() {
    AtomicInteger ref = new AtomicInteger(0);
    SagaStep<NoResult> saga = Sagas.voidRetryableAction("action#1", () -> {
      ref.incrementAndGet();
      throw new RuntimeException("action#1 failed");
    }, new RetryPolicy<NoResult>().withMaxRetries(2))
        .compensate("compensation#1", ref::decrementAndGet);
    EvaluationResult<NoResult> evaluationResult = SagaManager.use(saga).transact();
    assertNotNull(evaluationResult);
    assertTrue(evaluationResult.isError());
    assertEquals(2, ref.get());
  }

  @Test
  public void actionConsumesSagaIdTest() {
    AtomicReference<String> ref = new AtomicReference<>();
    Saga<NoResult> saga = Sagas.voidAction("a1", ref::set)
        .withoutCompensation();
    EvaluationResult<NoResult> evaluationResult = SagaManager.use(saga).transact();
    assertNotNull(evaluationResult);
    assertTrue(evaluationResult.isSuccess());
    assertEquals(evaluationResult.getEvaluationHistory().getSagaId(), ref.get());
  }

  @Test
  public void repeatableCompensationConsumesSagaIdTest() {
    AtomicReference<String> ref = new AtomicReference<>("");
    SagaStep<NoResult> saga = Sagas.voidAction("action#1", () -> {
      throw new RuntimeException("action#1 failed");
    })
        .compensate("compensation#1", ref::set, new RetryPolicy<>().withMaxRetries(2));
    EvaluationResult<NoResult> evaluationResult = SagaManager.use(saga).transact();
    assertNotNull(evaluationResult);
    String sagaId = evaluationResult.getEvaluationHistory().getSagaId();
    assertEquals(sagaId, ref.get());
  }

  @Test
  public void actionThrowsTest() {
    assertThrows(SagaActionFailedException.class,
        () -> SagaManager.use(Sagas.actionThrows("a1", new RuntimeException("action a1 is failed"))
            .compensate(Sagas.emptyCompensation("c1")))
            .transact()
            .orElseThrow());
  }

  @Test
  public void emptyCompensationTest() {
    Saga<Object> saga = Sagas.actionThrows("a1", new RuntimeException("action a1 is failed"))
        .compensate(Sagas.emptyCompensation("c1"));
    EvaluationResult<Object> er = SagaManager.use(saga).transact();
    assertEquals(2, er.getEvaluationHistory().getEvaluations().size());
    boolean allEvaluationsPresent = er.getEvaluationHistory().getEvaluations()
        .stream()
        .allMatch(e -> Arrays.asList("a1", "c1").contains(e.getName()));
    assertTrue(allEvaluationsPresent);
  }

  @Test
  public void compensationThrowsTest() {
    Saga<Object> saga = Sagas.actionThrows("a1", new RuntimeException("action a1 is failed"))
        .compensate(Sagas.compensationThrows("c1", new RuntimeException("compensation c1 is failed")));
    EvaluationResult<Object> er = SagaManager.use(saga).transact();
    assertThrows(SagaCompensationFailedException.class, er::orElseThrow);
  }

  @Test
  public void foldEvaluationResultTest() {
    Saga<Object> saga = Sagas.actionThrows("a1", new RuntimeException("action a1 is failed"))
        .compensate(Sagas.compensationThrows("c1", new RuntimeException("compensation c1 is failed")));
    String foldEvaluationResult = SagaManager.use(saga)
        .withId("foldEvaluationResultTest")
        .transact()
        .fold(v -> "no result", Throwable::getMessage);
    assertEquals("Compensation for saga foldEvaluationResultTest is failed while compensates c1 action", foldEvaluationResult);
  }

  @Test
  public void adaptErrorTest() {
    Saga<Object> saga = Sagas.actionThrows("a1", new RuntimeException("action a1 is failed"))
        .compensate(Sagas.compensationThrows("c1", new RuntimeException("compensation c1 is failed")));
    Object foldEvaluationResult = SagaManager.use(saga)
        .withId("foldEvaluationResultTest")
        .transact()
        .adaptError(err -> new NoResult())
        .valueOrThrow();
    assertNotNull(foldEvaluationResult);
  }
}
