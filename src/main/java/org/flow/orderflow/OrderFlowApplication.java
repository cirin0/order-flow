package org.flow.orderflow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableJpaRepositories("org.flow.orderflow.repository")
@EnableAsync
public class OrderFlowApplication {
  public static void main(String[] args) {
    SpringApplication.run(OrderFlowApplication.class, args);
  }
}
