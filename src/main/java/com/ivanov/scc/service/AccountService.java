package com.ivanov.scc.service;

import com.ivanov.scc.client.response.Accounts;
import com.ivanov.scc.model.Account;

public interface AccountService {
    Accounts getAllAccounts();
    Account getAccountForId(String uid);
}