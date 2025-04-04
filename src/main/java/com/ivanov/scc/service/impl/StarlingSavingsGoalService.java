package com.ivanov.scc.service.impl;

import com.ivanov.scc.client.StarlingApiClient;
import com.ivanov.scc.client.request.SavingGoalPutRequest;
import com.ivanov.scc.client.response.PutMoneyResponse;
import com.ivanov.scc.model.Amount;
import com.ivanov.scc.service.SavingsGoalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class StarlingSavingsGoalService implements SavingsGoalService {
    private static final String PUT_SAVING_GOALS = "/api/v2/account/%s/savings-goals/%s/add-money/%s";

    private final StarlingApiClient apiClient;

    @Autowired
    public StarlingSavingsGoalService(StarlingApiClient apiClient) {
        this.apiClient = apiClient;
    }

    @Override
    public PutMoneyResponse putMoneyToSavingsGoal(String savingGoalUid, String accountUid, Amount amount) {
        SavingGoalPutRequest savingGoalPutRequest = new SavingGoalPutRequest();
        savingGoalPutRequest.setAmount(amount);

        return apiClient.put(
                String.format(PUT_SAVING_GOALS, accountUid, savingGoalUid, UUID.randomUUID()),
                savingGoalPutRequest,
                PutMoneyResponse.class
        );
    }
}
