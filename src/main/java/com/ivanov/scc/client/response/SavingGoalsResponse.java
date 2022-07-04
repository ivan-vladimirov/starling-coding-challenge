package com.ivanov.scc.client.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ivanov.scc.model.FeedItem;
import com.ivanov.scc.model.SavingGoal;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SavingGoalsResponse {
    @JsonProperty("savingsGoalList")
    private List<SavingGoal> savingGoal;

    public List<SavingGoal> getSavingGoal() {
        return savingGoal;
    }

    public void setSavingGoal(List<SavingGoal> savingGoal) {
        this.savingGoal = savingGoal;
    }
}
