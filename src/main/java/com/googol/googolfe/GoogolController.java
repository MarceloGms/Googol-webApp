package com.googol.googolfe;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;

@Controller
public class GoogolController {

   // for testing only
   private List<Result> searchResults;

   public GoogolController(List<Result> searchResults) {
      this.searchResults = SearchResultsGenerator.generateExampleSearchResults();
   }
  
   @GetMapping("/")
   public String showLandingPage() {
      return "redirect:/search";
   }

   @GetMapping("/search")
   public String showSearchPage() {
      return "search";
   }

   @GetMapping("/search/results")
   public String showResultsPage(Model model, @RequestParam() String query, @RequestParam(defaultValue = "0") String page) {
      int pageSize = 10;
      int totalPages = (int) Math.ceil((double) searchResults.size() / pageSize);

      int startIndex = Integer.parseInt(page) * pageSize;
      int endIndex = Math.min(startIndex + pageSize, searchResults.size());
      List<Result> resultsForPage = searchResults.subList(startIndex, endIndex);

      model.addAttribute("group", resultsForPage);
      model.addAttribute("query", query);
      model.addAttribute("page", page);
      model.addAttribute("totalPages", totalPages);

      return "results";
   }

   @GetMapping("/search/sub-urls")
   public String showSubUrlsPage(Model model, @RequestParam() String url) {
      model.addAttribute("url", url);
      return "urls";
   }

   @GetMapping("/admin")
   public String showAdminPage() {
      return "admin";
   }
}