package com.ivanov.scc.controller;

import com.ivanov.scc.model.Amount;
import com.ivanov.scc.service.impl.StarlingRoundingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/roundup")
public class RoundingController {
    private final StarlingRoundingService roundingService;

    @Autowired
    public RoundingController(StarlingRoundingService roundingService) {
        this.roundingService = roundingService;
    }

    @GetMapping
    public List<Amount> roundUpAllAccounts() {
        return roundingService.roundUpTransactionsForAllAccount();
    }

    @GetMapping("/byAccount")
    public Amount roundUpByAccountUid(@RequestParam String accountUid) {
        return roundingService.roundUpTransactionForAccount(accountUid);
    }
}
