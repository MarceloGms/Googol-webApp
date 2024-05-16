package com.googol.googolfe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.bind.annotation.ResponseBody;
import java.util.ArrayList;

@RestController
public class HackerNewsController {
    private static final Logger logger = LoggerFactory.getLogger(HackerNewsController.class);

    @PostMapping("/sendHackerNews")
   public ResponseEntity<String> sendUrlToServer(@RequestBody HNRequestBody requestBody) {
      String query = requestBody.getQuery();
      logger.info("Received query for Hacker News: " + query);
      // TODO: fazer merdas do hacker news
      
      return ResponseEntity.status(HttpStatus.OK).body("query processed successfully");
   }

    @GetMapping("/topstories")
    @ResponseBody
    private List<HackerNewsItemRecord> hackerNewsTopStories(@RequestParam(name="search", required=false, defaultValue="") String searchWord) {
        // TODO: Get IDs of top stories
        System.out.println("Entrei no endpoint top stories!");

        List<HackerNewsItemRecord> resultado = new ArrayList<>();

        String topStoriesEndpoint = "https://hacker-news.firebaseio.com/v0/topstories.json?print=pretty";

        RestTemplate restTemplate = new RestTemplate();
        List hackerNewsNewTopStories = restTemplate.getForObject(topStoriesEndpoint, List.class);

        int counter = 0;
        for (Object storyId :
                hackerNewsNewTopStories) {
            System.out.println("One story id: "+storyId);
            String storyURL = "https://hacker-news.firebaseio.com/v0/item/" + storyId + ".json?print=pretty";

            HackerNewsItemRecord oneStory = restTemplate.getForObject(storyURL, HackerNewsItemRecord.class);

            if(searchWord!="" && oneStory.title().contains(searchWord)){
                resultado.add(oneStory);
            } else if (searchWord=="") {
                resultado.add(oneStory);
            }


            counter+=1;
            if(counter>4)
                break;
        }

        return resultado;
    }
}
