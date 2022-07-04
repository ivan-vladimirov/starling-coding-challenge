package com.ivanov.scc.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GoalTarget {
    @JsonProperty("currency")
    private String currency;
}
