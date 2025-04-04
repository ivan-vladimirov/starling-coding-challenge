package com.ivanov.scc.service;

import com.ivanov.scc.api.dto.PutMoneyResponse;
import com.ivanov.scc.model.Amount;

public interface SavingsGoalService {
    PutMoneyResponse putMoneyToSavingsGoal(String savingGoalUid, String accountUid, Amount amount);
}