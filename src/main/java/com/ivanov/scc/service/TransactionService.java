package com.ivanov.scc.service;


import com.ivanov.scc.client.response.Transactions;
import com.ivanov.scc.model.Account;

import java.time.ZonedDateTime;
import java.util.List;

public interface TransactionService {
    List<Transactions> getAllTransactionsForAllAccounts(ZonedDateTime fromDate);
    Transactions getTransactionsForAccount(Account account, ZonedDateTime fromDate);
}
