package dev.rmaiun;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import java.util.stream.IntStream;

public class Example {

  static List<String> data = new ArrayList<>();

  public static void main(String[] args) {
    // CompletableFuture.completedFuture()
    // CompletableFuture<String> cf = new CompletableFuture<>();
    // cf.thenApply()
    SagaAction<String> action = () -> generateString(4);
    SagaCompensation compensation = () -> removeString("****");
    Saga<String> saga1 = Saga.action(() -> generateString(4)).compensate(() -> removeString("****"));
    Sagas.saga(saga1).then(x -> () -> removeString(x))
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
}
