package com.ivanov.scc.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Amount implements Serializable {
    @JsonProperty("currency")
    private String currency;
    @JsonProperty("minorUnits")
    private BigDecimal minorUnits;

    public Amount(String currency, BigDecimal minorUnits) {
        this.currency = currency;
        this.minorUnits = minorUnits;
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
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Amount amount = (Amount) o;
        return Objects.equals(currency, amount.currency) && Objects.equals(minorUnits, amount.minorUnits);
    }

    @Override
    public int hashCode() {
        return Objects.hash(currency, minorUnits);
    }
}
