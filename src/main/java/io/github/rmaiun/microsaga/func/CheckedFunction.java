package io.github.rmaiun.microsaga.func;

@FunctionalInterface
public interface CheckedFunction<T, R> {
  R apply(T t) throws Exception;
}
