package com.ivanov.scc.service.impl;

import com.ivanov.scc.client.StarlingApiClient;
import com.ivanov.scc.client.response.Accounts;
import com.ivanov.scc.common.HttpCode;
import com.ivanov.scc.exception.AccountsNotFoundException;
import com.ivanov.scc.exception.HttpNoOkResponse;
import com.ivanov.scc.model.Account;
import com.ivanov.scc.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StarlingAccountService implements AccountService {
    private final StarlingApiClient apiClient;
    private static final String GET_ACCOUNTS = "/api/v2/accounts";

    @Autowired
    public StarlingAccountService(StarlingApiClient apiClient) {
        this.apiClient = apiClient;
    }

    @Override
    public Accounts getAllAccounts() {
        try {
            return apiClient.get(GET_ACCOUNTS, Accounts.class);
        } catch (HttpNoOkResponse e) {
            if (e.getCode() == HttpCode.NOT_FOUND) {
                throw new AccountsNotFoundException("Accounts not found for current user.");
            }
            throw e;
        }
    }

    @Override
    public Account getAccountForId(String uid) {
        return getAllAccounts().getAccounts()
                .stream()
                .filter(account -> account.getAccountUid().equals(uid))
                .findAny()
                .orElse(null);
    }
}
