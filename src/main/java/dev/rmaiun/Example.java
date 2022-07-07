package dev.rmaiun;

import dev.rmaiun.component.SagaManager;
import dev.rmaiun.saga.Saga;
import dev.rmaiun.saga.SagaStep;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import net.jodah.failsafe.RetryPolicy;

public class Example {

  static List<String> data = new ArrayList<>();

  public static void main(String[] args) {
    Saga<String> generateStringStep = Sagas.action("generateString", () -> generateString(4))
        .compensate(Sagas.retryableCompensation("removeString", () -> removeString("****"),new RetryPolicy<>().withMaxRetries(3)));
    SagaStep<String> writeSomething = Sagas.action("writeSomething", () -> writeSomething("Hello"))
        .compensate(Sagas.compensation("cleanText", () -> cleanText("Hello")));

    Saga<Integer> hello = generateStringStep.then(writeSomething)
        .map(String::length);

    System.out.println(hello);
    Integer bla = new SagaManager().saga(hello).withName("bla").transactOrThrow();

    System.out.println(bla);
  }

  static String generateString(int qty) {
    System.out.println("Call 'generateString()'");
    String x = IntStream.range(0, qty)
        .mapToObj(a -> "*")
        .reduce("", (a, b) -> a + b);

    data.add(x);
    return x;
  }

  static void removeString(String str) {
    System.out.println("Call 'removeString()'");
    throw new RuntimeException("Problem with string removal");
    // data.removeIf(x -> x.equals(str));
  }

  static String writeSomething(String text) {
    System.out.println("Call 'writeSomething()'");
    throw new RuntimeException("bla");
  }

  static void cleanText(String text) {
    System.out.println("Call 'cleanText()'");

  }
}
