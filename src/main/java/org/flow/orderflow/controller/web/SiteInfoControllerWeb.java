package org.flow.orderflow.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SiteInfoControllerWeb {

  @GetMapping("/about")
  public String aboutUs(Model model) {
    model.addAttribute("pageTitle", "Про нас");
    return "about-contact/about";
  }

  @GetMapping("/contact")
  public String contactUs(Model model) {
    model.addAttribute("pageTitle", "Контакти");
    return "about-contact/contact";
  }

  @GetMapping("/privacy")
  public String privacy(Model model) {
    model.addAttribute("pageTitle", "Політика конфіденційності");
    return "privacy-term/privacy";
  }

  @GetMapping("/terms")
  public String terms(Model model) {
    model.addAttribute("pageTitle", "Умови використання");
    return "privacy-term/term";
  }
}
