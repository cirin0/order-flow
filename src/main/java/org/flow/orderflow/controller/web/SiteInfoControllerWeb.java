package org.flow.orderflow.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SiteInfoControllerWeb {

  @GetMapping("/about")
  public String aboutUs() {
    return "about-contact/about";
  }

  @GetMapping("/contact")
  public String contactUs() {
    return "about-contact/contact";
  }

  @GetMapping("/privacy")
  public String privacy() {
    return "privacy-term/privacy";
  }

  @GetMapping("/terms")
  public String terms() {
    return "privacy-term/term";
  }

}

