package com.ivanov.scc.service.impl;

import com.ivanov.scc.client.response.Transactions;
import com.ivanov.scc.model.Account;
import com.ivanov.scc.model.Amount;
import com.ivanov.scc.model.FeedItem;
import com.ivanov.scc.service.AccountService;
import com.ivanov.scc.service.RoundingCalculator;
import com.ivanov.scc.service.RoundingService;
import com.ivanov.scc.service.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class StarlingRoundingService implements RoundingService {

    private static final Logger LOG = LoggerFactory.getLogger(StarlingRoundingService.class);

    private final AccountService accountService;
    private final TransactionService transactionService;
    private final RoundingCalculator roundingCalculator;

    public StarlingRoundingService(AccountService accountService,
                                   TransactionService transactionService,
                                   RoundingCalculator roundingCalculator) {
        this.accountService = accountService;
        this.transactionService = transactionService;
        this.roundingCalculator = roundingCalculator;
    }

    @Override
    public List<Amount> roundUpTransactionsForAllAccount() {
        List<Amount> roundUps = new ArrayList<>();
        ZonedDateTime weekAgo = ZonedDateTime.now().minusDays(7);

        accountService.getAllAccounts().getAccounts().forEach(account -> {
            Transactions transactions = transactionService.getTransactionsForAccount(account, weekAgo);
            List<FeedItem> feedItems = transactions.getFeedItems();
            Amount roundUp = roundingCalculator.calculateRoundUps(account.getCurrency(), feedItems);
            roundUps.add(roundUp);
        });

        return roundUps;
    }

    @Override
    public Amount roundUpTransactionForAccount(String accountUid) {
        LOG.info("Rounding transactions for account with UID: {}", accountUid);
        Account account = accountService.getAccountForId(accountUid);
        if (account == null) {
            throw new IllegalArgumentException("Account not found for UID: " + accountUid);
        }

        ZonedDateTime weekAgo = ZonedDateTime.now().minusDays(7);
        Transactions transactions = transactionService.getTransactionsForAccount(account, weekAgo);
        return roundingCalculator.calculateRoundUps(account.getCurrency(), transactions.getFeedItems());
    }
}
