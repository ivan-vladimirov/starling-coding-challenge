package com.ivanov.scc.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.math.RoundingMode;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Amount {
    @JsonProperty("currency")
    private String currency;
    @JsonProperty("minorUnits")
    private BigDecimal minorUnits;

    public Amount(String currency, BigDecimal minorUnits) {
        this.currency = currency;
        this.minorUnits = minorUnits.divide(BigDecimal.valueOf(100));
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public BigDecimal getMinorUnits() {
        return minorUnits;
    }

    public void setMinorUnits(BigDecimal minorUnits) {
        this.minorUnits = minorUnits;
    }
}
