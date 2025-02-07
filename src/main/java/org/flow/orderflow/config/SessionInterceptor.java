package org.flow.orderflow.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.util.Set;

@Component
public class SessionInterceptor implements HandlerInterceptor {
  private final Set<String> protectedEndpoints;
  private final Set<String> authPages;

  public SessionInterceptor(Set<String> protectedEndpoints, Set<String> authPages) {
    this.protectedEndpoints = protectedEndpoints;
    this.authPages = authPages;
  }

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
    String requestURI = request.getRequestURI();
    HttpSession session = request.getSession(false);

    // Перевірка сторінок, потребують авторизації
    if (!protectedEndpoints.contains(requestURI)) {
      return true;
    }
    // Перевірка авторизованих сторінок
    if (authPages.contains(requestURI) && session != null && session.getAttribute("user") != null) {
      response.sendRedirect("/user/profile");
      return false;
    }

    // Перевірка захищених сторінок
    if (session == null || session.getAttribute("user") == null) {
      response.sendRedirect("/user/login");
      return false;
    }

    return true;

  }
}
