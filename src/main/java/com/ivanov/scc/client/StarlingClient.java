package com.ivanov.scc.client;

import com.ivanov.scc.client.response.SavingGoalsResponse;
import com.ivanov.scc.config.HttpClient;
import com.ivanov.scc.config.HttpCode;
import com.ivanov.scc.config.HttpNoOkResponse;
import com.ivanov.scc.exception.AccountsNotFoundException;
import com.ivanov.scc.model.Account;
import com.ivanov.scc.client.response.AccountResponse;
import com.ivanov.scc.client.response.TransactionsResponse;
import com.ivanov.scc.model.SavingGoal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class StarlingClient {
    private static final Logger LOG = LoggerFactory.getLogger(StarlingClient.class);
    private final static String GET_ACCOUNTS = "/api/v2/accounts";
    private final static String GET_TRANSACTIONS = "/api/v2/feed/account/%s/" +
            "category/%s?changesSince=%s";
    private final static String GET_SAVING_GOALS = "/api/v2/account/%s/savings-goals";
    private final HttpClient httpClient;
    @Autowired
    public StarlingClient(@Qualifier("starling-api") HttpClient httpClient){
        this.httpClient = httpClient;
    }

    public AccountResponse getAllAccounts(){
        try{
            return httpClient.sendGetWithJsonResponse(GET_ACCOUNTS, AccountResponse.class);
        } catch (HttpNoOkResponse e) {
            if (e.getCode() == HttpCode.NOT_FOUND){
                throw new AccountsNotFoundException("Accounts not found for current user.");
            }
            throw e;
        }
    }
    public List<SavingGoalsResponse> getAllSavingGoals(){
        List<Account> accounts = getAllAccounts().getAccounts();
        if(accounts == null || accounts.isEmpty()){
            LOG.error("No accounts to retrieve transactions from.");
            return null;
        }
        List<SavingGoalsResponse> savingGoalsResponses = new ArrayList<>();
        accounts.forEach(account -> {
            try{
                savingGoalsResponses.add(httpClient.sendGetWithJsonResponse( String.format(GET_SAVING_GOALS,account.getAccountUid()), SavingGoalsResponse.class));
            } catch (HttpNoOkResponse e) {
                if (e.getCode() == HttpCode.NOT_FOUND){
                    throw new AccountsNotFoundException("Saving Goals not found for current user.");
                }
                throw e;
            }
        });
        return savingGoalsResponses;
    }
    public void addMoneyToSavingGoal(String savingGoalUid, String accountUid){

    }

    public List<TransactionsResponse> getTransactions(ZonedDateTime fromDate){
        List<Account> accounts = getAllAccounts().getAccounts();
        if(accounts == null || accounts.isEmpty()){
            LOG.error("No accounts to retrieve transactions from.");
            return null;
        }
        List<TransactionsResponse> transactions = new ArrayList<>();

        accounts.forEach( account -> {
            try{
                transactions.add(httpClient.sendGetWithJsonResponse(
                        String.format(GET_TRANSACTIONS,
                                account.getAccountUid(),
                                account.getDefaultCategory(),
                                fromDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"))),
                        TransactionsResponse.class));
            }catch (HttpNoOkResponse e){
                if (e.getCode() == HttpCode.NOT_FOUND){
                    throw new AccountsNotFoundException("Transactions not found for current user.");
                }
                throw e;
            }

        });

        return transactions;
    }
}
