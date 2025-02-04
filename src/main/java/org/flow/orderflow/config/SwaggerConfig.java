package org.flow.orderflow.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

  @Bean
  public OpenAPI customOpenAPI() {

    Info info = new Info()
      .title("Order Flow API")
      .version("1.0")
      .description("API for Order Flow application");

    return new OpenAPI()
      .info(info);
  }
}
