package com.ivanov.scc.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Amount {
    @JsonProperty("currency")
    private String currency;
    @JsonProperty("minorUnits")
    private String minorUnits;

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getMinorUnits() {
        return minorUnits;
    }

    public void setMinorUnits(String minorUnits) {
        this.minorUnits = minorUnits;
    }
}
