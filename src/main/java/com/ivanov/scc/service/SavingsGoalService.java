package com.ivanov.scc.service;

import com.ivanov.scc.client.response.PutMoneyResponse;
import com.ivanov.scc.model.Amount;

public interface SavingsGoalService {
    PutMoneyResponse putMoneyToSavingsGoal(String savingGoalUid, String accountUid, Amount amount);
}