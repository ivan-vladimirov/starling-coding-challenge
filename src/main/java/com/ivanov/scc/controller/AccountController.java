package com.ivanov.scc.controller;

import com.ivanov.scc.model.Account;
import com.ivanov.scc.model.AccountResponse;
import com.ivanov.scc.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/codingTest")
public class AccountController {
    private final AccountService accountService;
    @Autowired
    public AccountController(AccountService accountService){
        this.accountService = accountService;
    }

    @GetMapping
    public AccountResponse getAllAccounts(){return accountService.retrieveAccounts();}



}
