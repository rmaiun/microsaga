package dev.rmaiun;

import dev.rmaiun.component.SagaManager;
import dev.rmaiun.saga.Saga;
import dev.rmaiun.saga.SagaStep;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import net.jodah.failsafe.RetryPolicy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Example {

  private static final Logger LOG = LogManager.getLogger(Example.class);
  static List<String> data = new ArrayList<>();

  public static void main(String[] args) {
    LOG.info("Hello");
    Saga<String> generateStringStep = Sagas.action("generateString", () -> generateString(4))
        .compensate(Sagas.retryableCompensation("removeString", () -> removeString("****"), new RetryPolicy<>().withMaxRetries(3)));
    SagaStep<String> writeSomething = Sagas.action("writeSomething", () -> writeSomething("Hello"))
        .compensate(Sagas.compensation("cleanSomething", () -> cleanSomething("Hello")));

    Saga<Integer> hello = generateStringStep.then(writeSomething)
        .map(String::length);

    Integer bla = new SagaManager().saga(hello).withName("bla").transactOrThrow();
  }

  static String generateString(int qty) {
    LOG.info("Call 'generateString()'");
    String x = IntStream.range(0, qty)
        .mapToObj(a -> "*")
        .reduce("", (a, b) -> a + b);

    data.add(x);
    return x;
  }

  static void removeString(String str) {
    LOG.info("Call 'removeString()'");
    // throw new RuntimeException("Problem with string removal");
    // data.removeIf(x -> x.equals(str));
  }

  static String writeSomething(String text) {
    LOG.info("Call 'writeSomething()'");
    // throw new RuntimeException("bla");
    return "";
  }

  static void cleanSomething(String text) {
    LOG.info("Call 'cleanSomething()'");
  }
}
