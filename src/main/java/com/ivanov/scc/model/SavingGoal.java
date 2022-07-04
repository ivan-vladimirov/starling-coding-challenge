package com.ivanov.scc.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SavingGoal {
    @JsonProperty("savingsGoalUid")
    private String savingsGoalUid;
    @JsonProperty("target")
    private GoalTarget target;
    @JsonProperty("totalSaved")
    private TotalSaved totalSaved;

    public String getSavingsGoalUid() {
        return savingsGoalUid;
    }

    public void setSavingsGoalUid(String savingsGoalUid) {
        this.savingsGoalUid = savingsGoalUid;
    }

    public GoalTarget getTarget() {
        return target;
    }

    public void setTarget(GoalTarget target) {
        this.target = target;
    }

    public TotalSaved getTotalSaved() {
        return totalSaved;
    }

    public void setTotalSaved(TotalSaved totalSaved) {
        this.totalSaved = totalSaved;
    }
}
