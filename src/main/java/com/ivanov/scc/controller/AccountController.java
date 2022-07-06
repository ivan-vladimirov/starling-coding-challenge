package com.ivanov.scc.controller;

import com.ivanov.scc.client.response.Accounts;
import com.ivanov.scc.client.StarlingClient;
import com.ivanov.scc.client.response.SavingGoalsResponse;
import com.ivanov.scc.client.response.Transactions;
import com.ivanov.scc.exception.AccountsNotFoundException;
import com.ivanov.scc.model.Account;
import com.ivanov.scc.model.Amount;
import com.ivanov.scc.service.RoundingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

@RestController
@RequestMapping("/api")
public class AccountController {
    private final StarlingClient accountService;
    private final RoundingService roundingService;
    @Autowired
    public AccountController(StarlingClient accountService, RoundingService roundingService){
        this.accountService = accountService;
        this.roundingService = roundingService;
    }

    @GetMapping("/accounts")
    public Accounts getAllAccounts(){return accountService.getAllAccounts();}
    /*
    Test endpoint that returns:
    |-> All transactions per account for the last day (hence why Lost of TransactionResponse)
     */
    @GetMapping("/transactions")
    public List<Transactions> getAllTransactions(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime fromDate){
        return accountService.getAllTransactionsForAllAccounts(fromDate);
    }
    @GetMapping("/transactions/byId")
    public Transactions getTransactionsForAcount(@RequestParam String accountUid, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime fromDate){
        Account account = accountService.getAccountForId(accountUid);
        if(account == null)
            throw new AccountsNotFoundException("Account could not be found for id" + accountUid);

        return accountService.getTransactionsForAccount(account, fromDate);
    }
    /*
    Test endpoint that is returning:
    |-> List of all the roundings per accounts for the last wek
    The amount are in MinorUnit same as in StarlingAPI.
     */
    @GetMapping("/roundUpWeek")
    public List<Amount> roundMeUp(){
         return roundingService.roundUpTransactionsForAccount();
    }
    @GetMapping("/getSavingGoals")
    public List<SavingGoalsResponse> getAllSavingGoals(){
        return accountService.getAllSavingGoals();
    }
    @PutMapping("/putMoneyInSavingGoal")
    public Object putMoneyInSavingGoal(@RequestParam String savingGoalUid, @RequestParam String accountUid,
                                     @RequestParam BigDecimal minor, @RequestParam String currency){
        return accountService.putMoneyToSavingGoal(savingGoalUid, accountUid, minor, currency);
    }
}
