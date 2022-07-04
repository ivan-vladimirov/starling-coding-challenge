package com.ivanov.scc.controller;

import com.ivanov.scc.client.response.AccountResponse;
import com.ivanov.scc.client.StarlingClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/account")
public class AccountController {
    private final StarlingClient accountService;
    @Autowired
    public AccountController(StarlingClient accountService){
        this.accountService = accountService;
    }

    @GetMapping("/accounts")
    public AccountResponse getAllAccounts(){return accountService.getAllAccounts();}

    @PutMapping("/savingGoal")
    public void addMoneyToSavingGoal(String savingGoalUid, String accountUid){ accountService.addMoneyToSavingGoal(savingGoalUid,accountUid);}

    @GetMapping("/transactions")
    public List<Object> getTransactions(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime fromDate){
        return accountService.getTransactions(fromDate);
    }

}
