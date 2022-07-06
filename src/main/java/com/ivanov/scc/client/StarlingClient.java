package com.ivanov.scc.client;

import com.ivanov.scc.client.request.SavingGoalPutRequest;
import com.ivanov.scc.client.response.PutMoneyResponse;
import com.ivanov.scc.config.HttpClient;
import com.ivanov.scc.config.HttpCode;
import com.ivanov.scc.config.HttpNoOkResponse;
import com.ivanov.scc.exception.AccountsNotFoundException;
import com.ivanov.scc.exception.TransactionsNotFoundException;
import com.ivanov.scc.model.Account;
import com.ivanov.scc.client.response.Accounts;
import com.ivanov.scc.client.response.Transactions;
import com.ivanov.scc.model.Amount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class StarlingClient {
    private static final Logger LOG = LoggerFactory.getLogger(StarlingClient.class);
    private final static String GET_ACCOUNTS = "/api/v2/accounts";
    private final static String GET_TRANSACTIONS = "/api/v2/feed/account/%s/" +
            "category/%s?changesSince=%s";
    private final static String GET_SAVING_GOALS = "/api/v2/account/%s/savings-goals";
    private final static String PUT_SAVING_GOALS = "/api/v2/account/%s/savings-goals/%s/add-money/%s";

    private final HttpClient httpClient;

    @Autowired
    public StarlingClient(@Qualifier("starling-api") HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public Accounts getAllAccounts() {
        try {
            return httpClient.sendGetWithJsonResponse(GET_ACCOUNTS, Accounts.class);
        } catch (HttpNoOkResponse e) {
            if (e.getCode() == HttpCode.NOT_FOUND) {
                throw new AccountsNotFoundException("Accounts not found for current user.");
            }
            throw e;
        }
    }

    public Account getAccountForId(String uid) {
        try {
            return httpClient.sendGetWithJsonResponse(GET_ACCOUNTS, Accounts.class)
                    .getAccounts()
                    .stream()
                    .filter(account -> account.getAccountUid().equals(uid))
                    .findAny()
                    .orElse(null);
        } catch (HttpNoOkResponse e) {
            if (e.getCode() == HttpCode.NOT_FOUND) {
                throw new AccountsNotFoundException("Accounts not found for current user.");
            }
            throw e;
        }
    }

    public PutMoneyResponse putMoneyToSavingsGoal(String savingGoalUid, String accountUid, Amount amount) {

        SavingGoalPutRequest savingGoalPutRequest = new SavingGoalPutRequest();
        savingGoalPutRequest.setAmount(amount);

        return httpClient.sendPutWithJsonResponse(String.format(PUT_SAVING_GOALS,
                accountUid, savingGoalUid, UUID.randomUUID()), savingGoalPutRequest, PutMoneyResponse.class);
    }

    public List<Transactions> getAllTransactionsForAllAccounts(ZonedDateTime fromDate) {
        List<Account> accounts = getAllAccounts().getAccounts();
        List<Transactions> transactionsAllAccounts = new ArrayList<>();
        if (accounts == null || accounts.isEmpty()) {
            LOG.error("No account to retrieve transactions from.");
            return null;
        }
        accounts.forEach(account -> transactionsAllAccounts.add(getTransactionsForAccount(account, fromDate)));
        return transactionsAllAccounts;
    }

    public Transactions getTransactionsForAccount(Account account, ZonedDateTime fromDate) {
        try {
            return httpClient.sendGetWithJsonResponse(
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
