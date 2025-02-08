package org.flow.orderflow.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.flow.orderflow.service.AuthenticationService;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SessionInterceptor implements HandlerInterceptor {
  private final AuthenticationService authenticationService;

  private final List<String> protectedPaths = Arrays.asList(
    "/cart/**",
    "/order/**",
    "/user/profile",
    "/user/logout"
  );

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
    String requestURI = request.getRequestURI();

    boolean isProtectedPath = protectedPaths.stream()
      .anyMatch(requestURI::startsWith);

    if (!isProtectedPath) {
      return true;
    }

    HttpSession session = request.getSession();
    String sessionToken = (String) session.getAttribute("sessionToken");

    if (sessionToken == null || !authenticationService.validateSession(sessionToken)) {
      response.sendRedirect("/auth/login");
      return false;
    }
    return true;
  }
}
