package com.ivanov.scc.controller;

import com.ivanov.scc.client.response.Accounts;
import com.ivanov.scc.exception.AccountsNotFoundException;
import com.ivanov.scc.model.Account;
import com.ivanov.scc.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;

    @Autowired
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping
    public Accounts getAllAccounts() {
        return accountService.getAllAccounts();
    }

    @GetMapping("/{accountUid}")
    public Account getAccountById(@PathVariable String accountUid) {
        Account account = accountService.getAccountForId(accountUid);
        if (account == null)
            throw new AccountsNotFoundException("Account could not be found for id " + accountUid);
        return account;
    }
}
