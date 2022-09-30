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
    CheckedFunction<String, NoResult> stringNoResultCheckedFunction = SagaUtils
        .callableToCheckedFunc(SagaUtils.voidRunnableToCallable(action));
    return mkAction(name, stringNoResultCheckedFunction, invocations, retryPolicy);
  }

  public <A> SagaAction<A> mkAction(String name, CheckedFunction<String, A> action, List<SagaInvocation> invocations,
      RetryPolicy<A> retryPolicy) {
    Function<SagaInvocation, SagaAction<A>> existMapper = invocation -> invocation.isSuccess()
        ? Sagas.action(name, () -> {
          EvaluationData<A> data = strToObject(invocation.getResult());
          return data.getData();
        })
        : Sagas.actionThrows(name, new RuntimeException(formatExceptionText(invocation.getResult())));
    return invocations.stream()
        .filter(i -> !i.isCompensation() && i.getName().equals(name))
        .findAny()
        .map(existMapper)
        .orElseGet(() -> Sagas.retryableAction(name, action, retryPolicy));
  }

  public SagaCompensation mkCompensation(String name, Consumer<String> compensation, List<SagaInvocation> invocations) {
    return mkCompensation(name, compensation, invocations, SagaUtils.defaultRetryPolicy());
  }

  public SagaCompensation mkCompensation(String name, Consumer<String> compensation, List<SagaInvocation> invocations,
      RetryPolicy<Object> retryPolicy) {
    return invocations.stream()
        .filter(i -> i.isCompensation() && i.getName().equals(name))
        .findAny()
        .filter(SagaInvocation::isSuccess)
        .map(i -> Sagas.emptyCompensation(name))
        .orElseGet(() -> Sagas.retryableCompensation(name, compensation, retryPolicy));
  }

  private String formatExceptionText(String invocationErrorMsg) {
    EvaluationData<String> result = strToObject(invocationErrorMsg);
    return result.getData();
  }

  private <T> EvaluationData<T> strToObject(String result) {
    try {
      EvaluationData<T> data = objectMapper.readValue(result, new TypeReference<>() {
      });
      Class<?> clazz = data.getClassPath() != null
          ? Class.forName(data.getClassPath())
          : String.class;
      T subdata = data.getData();
      var z = objectMapper.convertValue(subdata, clazz);
      data.setData((T) z);
      return data;
    } catch (JsonProcessingException | ClassNotFoundException e) {
      throw new RuntimeException(e);
    }
  }
}
