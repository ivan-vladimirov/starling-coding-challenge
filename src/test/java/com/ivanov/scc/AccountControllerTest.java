package com.ivanov.scc;

import com.ivanov.scc.api.StarlingClient;
import com.ivanov.scc.api.dto.Accounts;
import com.ivanov.scc.api.dto.PutMoneyResponse;
import com.ivanov.scc.api.dto.Transactions;
import com.ivanov.scc.controller.AccountController;
import com.ivanov.scc.model.Account;
import com.ivanov.scc.model.Amount;
import com.ivanov.scc.service.RoundingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AccountControllerTest {
    @InjectMocks
    private AccountController accountController;
    @Mock
    private StarlingClient starlingClient;
    @Mock
    private RoundingService roundingService;

    @Test
    void testGetAllAccounts(){
        Account acc = new Account();
        List<Account> accountList = new ArrayList<>();
        accountList.add(acc);
        Accounts accounts = new Accounts();
        accounts.setAccounts(accountList);

        when(starlingClient.getAllAccounts()).thenReturn(accounts);

        assertEquals(accounts,accountController.getAllAccounts());
    }
    @Test
    void testGetAllTransactions(){
        Transactions transactions = new Transactions();
        List<Transactions> allAccountsTransactions = new ArrayList<>();
        allAccountsTransactions.add(transactions);

        when(starlingClient.getAllTransactionsForAllAccounts(any())).thenReturn(allAccountsTransactions);
        assertEquals(allAccountsTransactions,accountController.getAllTransactions(ZonedDateTime.now()));
    }

    @Test
    void testGetTransactionsById(){
        Transactions transactions = new Transactions();
        Account acc = new Account();
        when(starlingClient.getAccountForId("test")).thenReturn(acc);
        when(starlingClient.getTransactionsForAccount(any(), any())).thenReturn(transactions);
        assertEquals(transactions,accountController.getTransactionsForAccount("test",ZonedDateTime.now()));
    }

    @Test
    void testRoundUpWeekGet(){
        Amount amount = new Amount("TST", BigDecimal.valueOf(50.5));
        List<Amount> savings = new ArrayList<>();
        savings.add(amount);
        when(roundingService.roundUpTransactionsForAllAccount()).thenReturn(savings);
        assertEquals(savings,accountController.roundUpAll());
    }

    @Test
    void testRoundUpWeekByAccountId(){
        Amount amount = new Amount("TST", BigDecimal.valueOf(50.5));
        when(roundingService.roundUpTransactionForAccount("TST")).thenReturn(amount);
        assertEquals(amount,accountController.roundUpByAccountUid("TST"));
    }

    @Test
    void testRoundUpWeekPut(){
        Amount amount = new Amount("TST", BigDecimal.valueOf(50.5));
        PutMoneyResponse pmr = new PutMoneyResponse();
        when(starlingClient.putMoneyToSavingsGoal(eq("test"), eq("test"), any())).thenReturn(pmr);
        assertEquals(pmr,accountController.roundMeUpAndPutInSaving("test","test"));
    }
}
