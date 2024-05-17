package com.googol.googolfe;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
public class AdviceController {

    @GetMapping("/advice")
    public ResponseEntity<String> randomAdvice() {
        String adviceEndpoint = "https://api.adviceslip.com/advice";

        RestTemplate restTemplate = new RestTemplate();
        System.out.println("Received request for random advice");

        try {
            ResponseEntity<String> response = restTemplate.getForEntity(adviceEndpoint, String.class);
            String responseBody = response.getBody();
            System.out.println("Received response: " + responseBody);

            if (response.getStatusCode().is2xxSuccessful()) {
                // Parse JSON response
                ObjectMapper mapper = new ObjectMapper();
                AdviceResponse adviceResponse = mapper.readValue(responseBody, AdviceResponse.class);
                
                // Get the advice content
                if (adviceResponse != null && adviceResponse.getSlip() != null) {
                    String advice = adviceResponse.getSlip().getAdvice();
                    System.out.println("Returning advice: " + advice);
                    return ResponseEntity.ok(advice);
                } else {
                    return ResponseEntity.status(500).body("No advice found");
                }
            } else {
                return ResponseEntity.status(response.getStatusCode()).body("Error: " + responseBody);
            }
        } catch (HttpClientErrorException e) {
            e.printStackTrace();
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsString());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("An error occurred: " + e.getMessage());
        }
    }
}
