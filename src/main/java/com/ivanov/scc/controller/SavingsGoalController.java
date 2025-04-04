package com.ivanov.scc.controller;

import com.ivanov.scc.api.dto.PutMoneyResponse;
import com.ivanov.scc.model.Amount;
import com.ivanov.scc.service.SavingsGoalService;
import com.ivanov.scc.service.impl.StarlingRoundingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/savings-goal")
public class SavingsGoalController {

    private final StarlingRoundingService roundingService;
    private final SavingsGoalService savingsGoalService;

    @Autowired
    public SavingsGoalController(StarlingRoundingService roundingService, SavingsGoalService savingsGoalService) {
        this.roundingService = roundingService;
        this.savingsGoalService = savingsGoalService;
    }

    @PutMapping("/roundup")
    public PutMoneyResponse roundUpAndSave(
            @RequestParam String savingsGoalUid,
            @RequestParam String accountUid) {

        Amount roundUp = roundingService.roundUpTransactionForAccount(accountUid);
        return savingsGoalService.putMoneyToSavingsGoal(savingsGoalUid, accountUid, roundUp);
    }
}
