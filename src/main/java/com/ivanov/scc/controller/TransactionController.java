package com.ivanov.scc.controller;

import com.ivanov.scc.api.dto.Transactions;
import com.ivanov.scc.exception.AccountsNotFoundException;
import com.ivanov.scc.model.Account;
import com.ivanov.scc.service.AccountService;
import com.ivanov.scc.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZonedDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;
    private final AccountService accountService;

    @Autowired
    public TransactionController(TransactionService transactionService, AccountService accountService) {
        this.transactionService = transactionService;
        this.accountService = accountService;
    }

    @GetMapping
    public ResponseEntity<?> getAllTransactions(
            @RequestParam(required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime fromDate) {
        List<Transactions> transactions = transactionService.getAllTransactionsForAllAccounts(fromDate);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/byId")
    public Transactions getTransactionsForAccount(
            @RequestParam(required = true) String accountUid,
            @RequestParam(required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime fromDate) {
        Account account = accountService.getAccountForId(accountUid);
        if (account == null)
            throw new AccountsNotFoundException("Account could not be found for id " + accountUid);
        return transactionService.getTransactionsForAccount(account, fromDate);
    }
}
