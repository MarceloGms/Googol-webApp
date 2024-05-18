package com.googol.googolfe.web.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Slip class represents a slip containing advice from the external API.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Slip {
    @JsonProperty("slip_id")
    private int slipId;

    @JsonProperty("advice")
    private String advice;

    /**
     * Retrieves the ID of the slip.
     * @return the slip ID
     */
    public int getSlipId() {
        return slipId;
    }

    public void setSlipId(int slipId) {
        this.slipId = slipId;
    }

    /**
     * Retrieves the advice content.
     * @return the advice content
     */
    public String getAdvice() {
        return advice;
    }

    public void setAdvice(String advice) {
        this.advice = advice;
    }
    }