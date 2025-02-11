//package org.flow.orderflow.controller.api;
//
//import jakarta.servlet.RequestDispatcher;
//import jakarta.servlet.http.HttpServletRequest;
//import org.springframework.boot.web.servlet.error.ErrorController;
//import org.springframework.http.HttpStatus;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.RequestMapping;
//
//@Controller
//public class CustomErrorController implements ErrorController {
//
//  @RequestMapping("/error")
//  public String handleError(HttpServletRequest request, Model model) {
//    Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
//    String errorMessage = "Сталася помилка";
//    String errorDetails = "";
//
//    if (status != null) {
//      int statusCode = Integer.parseInt(status.toString());
//
//      if (statusCode == HttpStatus.NOT_FOUND.value()) {
//        errorMessage = "Сторінку не знайдено";
//        errorDetails = "Запитана сторінка не існує";
//      } else if (statusCode == HttpStatus.FORBIDDEN.value()) {
//        errorMessage = "Доступ заборонено";
//        errorDetails = "У вас немає прав для доступу до цієї сторінки";
//      } else if (statusCode == HttpStatus.METHOD_NOT_ALLOWED.value()) {
//        errorMessage = "Метод не підтримується";
//        errorDetails = "Даний тип запиту не підтримується для цього URL";
//      }
//    }
//
//    model.addAttribute("errorMessage", errorMessage);
//    model.addAttribute("errorDetails", errorDetails);
//
//    return "error"; // це вказує на error.html шаблон
//  }
//}
