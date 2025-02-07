package org.flow.orderflow.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Set;

@Configuration
public class WebConfig implements WebMvcConfigurer {

  @Override
  public void addInterceptors(InterceptorRegistry registry) {

    Set<String> protectedEndpoints = Set.of(
      "/user/profile",
      "/user/logout",
      "/cart/**",
      "/order/**"
    );

    Set<String> authPages = Set.of(
      "/user/login",
      "/user/register"
    );

    registry.addInterceptor(new SessionInterceptor(protectedEndpoints, authPages));
  }
}
