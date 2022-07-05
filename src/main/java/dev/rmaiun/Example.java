package dev.rmaiun;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class Example {

  static List<String> data = new ArrayList<>();

  public static void main(String[] args) {
    Saga<Integer> hello = Sagas.step("generateString", () -> generateString(4), () -> removeString("****"))
        .flatmap(z -> Sagas.step("writeSomething", () -> writeSomething("Hello"), () -> cleanText("Hello")))
        .map(String::length);

    System.out.println(hello);
    Integer bla = new SagaManager().saga(hello).withName("bla").transact();

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
    data.removeIf(x -> x.equals(str));
  }

  static String writeSomething(String text) {
    System.out.println("Call 'writeSomething()'");
    throw new RuntimeException("bla");
  }

  static void cleanText(String text) {
    System.out.println("Call 'cleanText()'");

  }
}
