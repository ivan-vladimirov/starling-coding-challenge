package com.ivanov.scc.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SavingGoal {
    @JsonProperty("savingsGoalUid")
    private String savingsGoalUid;
    @JsonProperty("target")
    private GoalTarget target;
}
