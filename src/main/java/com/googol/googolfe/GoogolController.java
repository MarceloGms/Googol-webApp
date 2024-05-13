package com.googol.googolfe;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;

@Controller
public class GoogolController {

   @GetMapping("/")
   public String showGoogolPage() {
      return "googol";
   }

   @GetMapping("/search")
   public String showSearchPage(Model model, @RequestParam() String query, @RequestParam(defaultValue = "0") String page) {
      List<Result> searchResults = new ArrayList<>();

      for (int i = 0; i < 30; i++) {
         String title = "Result " + (i + 1);
         String url = "http://example.com/result" + (i + 1);
         String citation = "Citation for Result " + (i + 1);
         searchResults.add(new Result(title, url, citation));
      }

      model.addAttribute("group", searchResults);
      model.addAttribute("query", query);
      model.addAttribute("page", page);

      return "search";
   }

   @GetMapping("/urls")
   public String showSubUrlsPage(Model model, @RequestParam() String url, @RequestParam(defaultValue = "0") String page) {
      List<String> urls = new ArrayList<>();
      for (int i = 1; i <= 30; i++) {
         urls.add("URL" + i);
      }
      model.addAttribute("url", url);
      model.addAttribute("urls", urls);
      return "urls";
   }

   @GetMapping("/admin")
   public String showAdminPage(Model model) {
      List<String> brls = new ArrayList<>();
      for (int i = 1; i <= 7; i++) {
         brls.add("Barrel " + i);
      }
      model.addAttribute("barrels", brls);
      model.addAttribute("searches", new String[] {"Search 1", "Search 2", "Search 3", "Search 4", "Search 5", "Search 6", "Search 7", "Search 8", "Search 9", "Search 10"});
      return "admin";
   }
}