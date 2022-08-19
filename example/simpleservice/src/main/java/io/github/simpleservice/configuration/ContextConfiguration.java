package io.github.simpleservice.configuration;

import io.github.rmaiun.microsaga.component.SagaManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ContextConfiguration {

  @Bean
  public RestTemplate restTemplate() {
    return new RestTemplate();
  }

  @Bean
  public SagaManager sagaManager() {
    return new SagaManager();
  }
}
