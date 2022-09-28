package io.github.simpleservice.helper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.rmaiun.microsaga.Sagas;
import io.github.rmaiun.microsaga.func.CheckedFunction;
import io.github.rmaiun.microsaga.saga.SagaAction;
import io.github.rmaiun.microsaga.saga.SagaCompensation;
import io.github.rmaiun.microsaga.support.EvaluationData;
import io.github.rmaiun.microsaga.support.NoResult;
import io.github.rmaiun.microsaga.util.SagaUtils;
import io.github.simpleservice.domain.SagaInvocation;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import net.jodah.failsafe.RetryPolicy;
import org.springframework.stereotype.Service;

@Service
public class SagaRequestHelper {

  private final ObjectMapper objectMapper;

  public SagaRequestHelper(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  public <A> SagaAction<A> mkAction(String name, CheckedFunction<String, A> action, List<SagaInvocation> invocations) {
    return mkAction(name, action, invocations, SagaUtils.defaultRetryPolicy());
  }

  public SagaAction<NoResult> mkAction(String name, Runnable action, List<SagaInvocation> invocations, RetryPolicy<NoResult> retryPolicy) {
    CheckedFunction<String, NoResult> stringNoResultCheckedFunction = SagaUtils.callableToCheckedFunc(SagaUtils.voidRunnableToCallable(action));
    return mkAction(name, stringNoResultCheckedFunction, invocations, retryPolicy);
  }

  public <A> SagaAction<A> mkAction(String name, CheckedFunction<String, A> action, List<SagaInvocation> invocations, RetryPolicy<A> retryPolicy) {
    Function<SagaInvocation, SagaAction<A>> existMapper = invocation ->
        invocation.isSuccess()
            ? Sagas.action(name, () -> strToObject(invocation.getResult()))
            : Sagas.action(name, () -> {
              throw new RuntimeException(invocation.getResult());
            });
    return invocations.stream()
        .filter(i -> !i.isCompensation() && i.getName().equals(name))
        .findAny()
        .map(existMapper)
        .orElseGet(() -> Sagas.retryableAction(name, action, retryPolicy));
  }

  public SagaCompensation mkCompensation(String name, Consumer<String> compensation, List<SagaInvocation> invocations) {
    return mkCompensation(name, compensation, invocations, SagaUtils.defaultRetryPolicy());
  }

  public SagaCompensation mkCompensation(String name, Consumer<String> compensation, List<SagaInvocation> invocations, RetryPolicy<Object> retryPolicy) {
    return invocations.stream()
        .filter(i -> i.isCompensation() && i.getName().equals(name))
        .findAny()
        .filter(SagaInvocation::isSuccess)
        .map(i -> Sagas.compensation(name, () -> {
        }))
        .orElseGet(() -> Sagas.retryableCompensation(name, compensation, retryPolicy));
  }

  private <T> T strToObject(String result) {
    try {
      return objectMapper.readValue(result, new TypeReference<EvaluationData<T>>() {
      }).getData();
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }
}
