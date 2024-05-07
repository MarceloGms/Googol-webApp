package com.googol.googolfe;

import java.util.ArrayList;
import java.util.List;

public class SearchResultsGenerator {

    public static List<Result> generateExampleSearchResults() {
        List<Result> searchResults = new ArrayList<>();

        for (int i = 0; i < 30; i++) {
            String title = "Result " + (i + 1);
            String url = "http://example.com/result" + (i + 1);
            String citation = "Citation for Result " + (i + 1);
            searchResults.add(new Result(title, url, citation));
        }
        return searchResults;
    }
}
