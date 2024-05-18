package com.googol.googolfe.web.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The AdviceResponse class represents the response structure from the external advice API.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AdviceResponse {
    @JsonProperty("slip")
    private Slip slip;

    /**
     * Retrieves the slip containing the advice.
     * @return the slip object
     */
    public Slip getSlip() {
        return slip;
    }

    public void setSlip(Slip slip) {
        this.slip = slip;
    }
}