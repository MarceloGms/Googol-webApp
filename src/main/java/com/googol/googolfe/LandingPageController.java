package com.googol.googolfe;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LandingPageController {
    @GetMapping("/")
    public String showLandingPage() {
      return "redirect:/search";
    }

    @GetMapping("/search")
    public String showSearchPage() {
      return "search";
    }
}