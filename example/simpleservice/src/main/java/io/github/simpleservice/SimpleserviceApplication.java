package io.github.simpleservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
public class SimpleserviceApplication {

  public static void main(String[] args) {
    SpringApplication.run(SimpleserviceApplication.class, args);
  }

}
