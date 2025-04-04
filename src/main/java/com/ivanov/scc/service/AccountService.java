package com.ivanov.scc.service;

import com.ivanov.scc.api.dto.Accounts;
import com.ivanov.scc.model.Account;

public interface AccountService {
    Accounts getAllAccounts();
    Account getAccountForId(String uid);
}