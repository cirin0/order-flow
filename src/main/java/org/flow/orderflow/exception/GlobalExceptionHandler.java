package org.flow.orderflow.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
  @ExceptionHandler(NotFound.class)
  public Object handleNotFound(NotFound e, HttpServletRequest request, Model model) {
    String accept = request.getHeader("Accept");

    if (accept != null && accept.contains("application/json")) {
      ErrorResponse errorResponse = new ErrorResponse(
        HttpStatus.NOT_FOUND,
        e.getMessage()
      );
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    } else {
      model.addAttribute("error", e.getMessage());
      model.addAttribute("status", HttpStatus.NOT_FOUND);
      return "error/404";
    }
  }
}
