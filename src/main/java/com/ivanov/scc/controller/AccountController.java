package com.ivanov.scc.controller;

import com.ivanov.scc.client.response.Accounts;
import com.ivanov.scc.client.StarlingClient;
import com.ivanov.scc.client.response.PutMoneyResponse;
import com.ivanov.scc.client.response.SavingGoalsResponse;
import com.ivanov.scc.client.response.Transactions;
import com.ivanov.scc.exception.AccountsNotFoundException;
import com.ivanov.scc.model.Account;
import com.ivanov.scc.model.Amount;
import com.ivanov.scc.service.RoundingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.List;

@RestController
@RequestMapping("/api")
public class AccountController {
    private final StarlingClient starlingClient;
    private final RoundingService roundingService;
    @Autowired
    public AccountController(StarlingClient starlingClient, RoundingService roundingService){
        this.starlingClient = starlingClient;
        this.roundingService = roundingService;
    }
    /*
    Test endpoint for retrieving all accounts. It returns:
    |-> Accounts object that has getAccounts()
     */
    @GetMapping("/accounts")
    public Accounts getAllAccounts(){return starlingClient.getAllAccounts();}
    /*
    Test endpoint that returns:
    |-> All transactions per account for the last day (hence why Lost of TransactionResponse)
     */
    @GetMapping("/transactions")
    public List<Transactions> getAllTransactions(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime fromDate){
        return starlingClient.getAllTransactionsForAllAccounts(fromDate);
    }
    /*
    Test endpoint for retrieving transactions for one account of choice. You must specify accoutnUID
    |-> returns Transactions object that contains all the feed items per that account.
     */
    @GetMapping("/transactions/byId")
    public Transactions getTransactionsForAccount(@RequestParam String accountUid, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime fromDate){
        Account account = starlingClient.getAccountForId(accountUid);
        if(account == null)
            throw new AccountsNotFoundException("Account could not be found for id" + accountUid);

        return starlingClient.getTransactionsForAccount(account, fromDate);
    }
    /*
    Test endpoint that is returning:
    |-> List of all the roundings per accounts for the last wek
    The amount are in MinorUnit same as in StarlingAPI.
     */
    @GetMapping("/roundUpWeek")
    public List<Amount> roundUpAll(){
         return roundingService.roundUpTransactionsForAllAccount();
    }
    /*
    Test endpoint for rounding up transactions for specific account. You must specify accountUid in parameters
    Returns Amount same way as the API
     */
    @GetMapping("/roundUpWeek/byAccountUid")
    public Amount roundUpByAccountUid(@RequestParam String accountUid){
        Amount roundUps = roundingService.roundUpTransactionForAccount(accountUid);
        return roundUps;
    }
    /*
    Test endpoint for rounding up all transactions per account and putting them in custom Savings Goal
    You must specify savingGoalUid and accountUid.
    |-> Returns same response as the API endpoint
     */
    @PutMapping("/roundUpWeek")
    public PutMoneyResponse roundMeUpAndPutInSaving(@RequestParam String savingGoalUid, @RequestParam String accountUid){
        Amount roundings = roundingService.roundUpTransactionForAccount(accountUid);
        return starlingClient.putMoneyToSavingGoal(savingGoalUid,accountUid,roundings);
    }
    /*
    Test endpoint for retrieving all savings Goals
    |-> Returns list of saving goals response
     */
    @GetMapping("/getSavingsGoals")
    public List<SavingGoalsResponse> getAllSavingGoals(){
        return starlingClient.getAllSavingGoals();
    }
}
