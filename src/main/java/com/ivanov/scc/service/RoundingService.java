package com.ivanov.scc.service;
import com.ivanov.scc.model.Amount;

import java.util.List;

public interface RoundingService {

    /**
     * Rounds up all outgoing transactions for all accounts from the past 7 days.
     *
     * @return A list of Amounts representing the total round-up per account.
     */
    List<Amount> roundUpTransactionsForAllAccount();

    /**
     * Rounds up outgoing transactions for a specific account from the past 7 days.
     *
     * @param accountUid The UID of the account.
     * @return An Amount representing the round-up total.
     */
    Amount roundUpTransactionForAccount(String accountUid);
}

