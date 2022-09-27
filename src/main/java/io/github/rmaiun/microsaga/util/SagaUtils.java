package io.github.rmaiun.microsaga.util;

import io.github.rmaiun.microsaga.func.CheckedFunction;
import io.github.rmaiun.microsaga.support.NoResult;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Function;
import net.jodah.failsafe.RetryPolicy;

public class SagaUtils {

  private SagaUtils() {
  }

  public static String defaultId() {
    return UUID.randomUUID().toString().replace("-", "");
  }

  public static <A> RetryPolicy<A> defaultRetryPolicy() {
    return new RetryPolicy<A>().withMaxRetries(0);
  }

  public static <A> CheckedFunction<String, A> funcAsChecked(Function<String, A> callable) {
    return callable::apply;
  }

  public static Callable<NoResult> voidRunnableToCallable(Runnable r) {
    return () -> {
      r.run();
      return NoResult.instance();
    };
  }

  public static <A> CheckedFunction<String, A> callableToCheckedFunc(Callable<A> c) {
    return sagaId -> c.call();
  }

  public static CheckedFunction<String, NoResult> consumerToCheckedFunc(Consumer<String> c) {
    return sagaId -> {
      c.accept(sagaId);
      return NoResult.instance();
    };
  }

  public static CheckedFunction<String, NoResult> runnableToCheckedFunc(Runnable r) {
    return sagaId -> voidRunnableToCallable(r).call();
  }
}
