package org.flow.orderflow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories("org.flow.orderflow.repository")
public class OrderFlowApplication {
  public static void main(String[] args) {
    SpringApplication.run(OrderFlowApplication.class, args);
  }
}
