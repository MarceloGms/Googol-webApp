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
      int pageSize = 10;
      int totalPages = (int) Math.ceil((double) searchResults.size() / pageSize);

      int startIndex = Integer.parseInt(page) * pageSize;
      int endIndex = Math.min(startIndex + pageSize, searchResults.size());
      List<Result> resultsForPage = searchResults.subList(startIndex, endIndex);

      model.addAttribute("group", resultsForPage);
      model.addAttribute("query", query);
      model.addAttribute("page", page);
      model.addAttribute("totalPages", totalPages);

      return "search";
   }

   @GetMapping("/urls")
   public String showSubUrlsPage(Model model, @RequestParam() String url, @RequestParam(defaultValue = "0") String page) {
      List<String> urls = new ArrayList<>();
      for (int i = 1; i <= 30; i++) {
         urls.add("URL" + i);
      }
      int pageSize = 10;
      int totalPages = (int) Math.ceil((double) urls.size() / pageSize);

      int startIndex = Integer.parseInt(page) * pageSize;
      int endIndex = Math.min(startIndex + pageSize, urls.size());
      List<String> subUrls = urls.subList(startIndex, endIndex);
      model.addAttribute("url", url);
      model.addAttribute("urls", subUrls);
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