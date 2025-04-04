package com.ivanov.scc.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ivanov.scc.model.Amount;

import java.io.Serializable;

public class SavingGoalPutRequest implements Serializable {
    @JsonProperty("amount")
    private Amount amount;

    public Amount getAmount() {
        return amount;
    }

    public void setAmount(Amount amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "SavingGoalPutRequest{" +
                "amount=" + amount +
                '}';
    }
}
