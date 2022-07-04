package dev.rmaiun;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class Example {

  static List<String> data = new ArrayList<>();

  public static void main(String[] args) {
    Saga<Integer> hello = Sagas.step(() -> generateString(4), () -> removeString("****"))
        .flatmap(z -> Sagas.step(() -> writeSomething("Hello"), () -> cleanText("Hello")))
        .map(String::length);

    System.out.println(hello);
    Integer bla = new SagaManager().saga(hello).withName("bla").transact();

    System.out.println(bla);
  }

  static String generateString(int qty) {
    String x = IntStream.range(0, qty)
        .mapToObj(a -> "*")
        .reduce("", (a, b) -> a + b);

    data.add(x);
    return x;
  }

  static void removeString(String str) {
    data.removeIf(x -> x.equals(str));
  }

  static String writeSomething(String text) {
    System.out.println(text);
    return text;
  }

  static void cleanText(String text) {
    System.out.printf("Clean %s \n", text);
  }
}
