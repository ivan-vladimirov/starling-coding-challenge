package com.ivanov.scc.service.impl;

import com.ivanov.scc.client.StarlingApiClient;
import com.ivanov.scc.client.response.Transactions;
import com.ivanov.scc.common.HttpCode;
import com.ivanov.scc.exception.HttpNoOkResponse;
import com.ivanov.scc.exception.TransactionsNotFoundException;
import com.ivanov.scc.model.Account;
import com.ivanov.scc.service.AccountService;
import com.ivanov.scc.service.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class StarlingTransactionService implements TransactionService {
    private static final Logger LOG = LoggerFactory.getLogger(StarlingTransactionService.class);
    private static final String GET_TRANSACTIONS = "/api/v2/feed/account/%s/category/%s?changesSince=%s";

    private final StarlingApiClient apiClient;
    private final AccountService accountService;

    @Autowired
    public StarlingTransactionService(StarlingApiClient apiClient, AccountService accountService) {
        this.apiClient = apiClient;
        this.accountService = accountService;
    }

    @Override
    public List<Transactions> getAllTransactionsForAllAccounts(ZonedDateTime fromDate) {
        List<Account> accounts = accountService.getAllAccounts().getAccounts();
        List<Transactions> transactionsAllAccounts = new ArrayList<>();

        if (accounts == null || accounts.isEmpty()) {
            LOG.error("No account to retrieve transactions from.");
            return List.of();
        }

        accounts.forEach(account -> transactionsAllAccounts.add(getTransactionsForAccount(account, fromDate)));
        return transactionsAllAccounts;
    }

    @Override
    public Transactions getTransactionsForAccount(Account account, ZonedDateTime fromDate) {
        try {
            return apiClient.get(
                    String.format(GET_TRANSACTIONS,
                            account.getAccountUid(),
                            account.getDefaultCategory(),
                            fromDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"))),
                    Transactions.class);
        } catch (HttpNoOkResponse e) {
            if (e.getCode() == HttpCode.NOT_FOUND) {
                throw new TransactionsNotFoundException("Transactions not found for current user.");
            }
            throw e;
        }
    }
}
