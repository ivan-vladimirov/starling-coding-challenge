package com.ivanov.scc.controller;

import com.ivanov.scc.client.response.AccountResponse;
import com.ivanov.scc.client.StarlingClient;
import com.ivanov.scc.client.response.TransactionsResponse;
import com.ivanov.scc.service.RoundingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/account")
public class AccountController {
    private final StarlingClient accountService;
    private final RoundingService roundingService;
    @Autowired
    public AccountController(StarlingClient accountService, RoundingService roundingService){
        this.accountService = accountService;
        this.roundingService = roundingService;
    }

    @GetMapping("/accounts")
    public AccountResponse getAllAccounts(){return accountService.getAllAccounts();}

    @PutMapping("/savingGoal")
    public void addMoneyToSavingGoal(String savingGoalUid, String accountUid){ accountService.addMoneyToSavingGoal(savingGoalUid,accountUid);}

    @GetMapping("/transactions")
    public List<TransactionsResponse> getTransactions(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime fromDate){
        return accountService.getTransactions(fromDate);
    }
    @GetMapping("/roundUpWeek")
    public List<Map<String, BigDecimal>> roundMeUp(){
         return roundingService.roundUpTransactions();
    }
}
