package com.ivanov.scc;

import com.ivanov.scc.client.StarlingClient;
import com.ivanov.scc.client.response.Accounts;
import com.ivanov.scc.client.response.PutMoneyResponse;
import com.ivanov.scc.client.response.Transactions;
import com.ivanov.scc.config.HttpClient;
import com.ivanov.scc.config.HttpNoOkResponse;
import com.ivanov.scc.exception.AccountsNotFoundException;
import com.ivanov.scc.exception.TransactionsNotFoundException;
import com.ivanov.scc.model.Account;
import com.ivanov.scc.model.Amount;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)

public class StarlingClientTest {
    @Mock
    HttpClient httpClient;
    @Captor
    ArgumentCaptor<String> uriCaptor;
    StarlingClient starlingClient;

    @BeforeEach
    void setup(){starlingClient = new StarlingClient(httpClient); }

    @Test
    void testGetAccountsNotFound(){
        lenient().when(httpClient.sendGetWithJsonResponse(uriCaptor.capture(), eq(Accounts.class)))
                .thenThrow(new HttpNoOkResponse("REQ", "RESP", HttpStatus.SC_NOT_FOUND));
        AccountsNotFoundException e = assertThrows(AccountsNotFoundException.class, () -> starlingClient.getAllAccounts());
        assertEquals("Accounts not found for current user.", e.getMessage());
    }
    @Test
    void testGetTransactionsNotFound(){
        Account eur = new Account();
        eur.setCurrency("EUR");

        List<Account> accounts = new ArrayList<>();
        accounts.add(eur);

        Accounts accountResponse = new Accounts();
        accountResponse.setAccounts(accounts);
        lenient().when(httpClient.sendGetWithJsonResponse(eq("/api/v2/accounts"), eq(Accounts.class)))
                .thenReturn(accountResponse);

        lenient().when(httpClient.sendGetWithJsonResponse(uriCaptor.capture(), eq(Transactions.class)))
                .thenThrow(new HttpNoOkResponse("REQ", "RESP", HttpStatus.SC_NOT_FOUND));
        TransactionsNotFoundException e = assertThrows(TransactionsNotFoundException.class,
                () -> starlingClient.getAllTransactionsForAllAccounts(ZonedDateTime.now()));
        assertEquals("Transactions not found for current user.", e.getMessage());
    }

    @Test
    void testGetAllAccounts(){
        lenient().when(httpClient.sendGetWithJsonResponse(uriCaptor.capture(), eq(Accounts.class)))
                .thenReturn(new Accounts());
        Accounts accounts = starlingClient.getAllAccounts();

        assertNotNull(accounts);
    }

    @Test
    void testGetTransactions(){
        Account acc = new Account();
        List<Account> accounts = new ArrayList<>();
        accounts.add(acc);

        Accounts accountResponse = new Accounts();
        accountResponse.setAccounts(accounts);

        lenient().when(httpClient.sendGetWithJsonResponse(eq("/api/v2/accounts"), eq(Accounts.class)))
                .thenReturn(accountResponse);
        lenient().when(httpClient.sendGetWithJsonResponse(uriCaptor.capture(), eq(Transactions.class)))
                .thenReturn(new Transactions());
        ZonedDateTime lt = ZonedDateTime.now();
        List<Transactions> transactions = starlingClient.getAllTransactionsForAllAccounts(lt);

        assertNotNull(transactions);
    }

    @Test
    void testPutMoneyInSavingGoal(){
        Amount amm = new Amount("GBP",BigDecimal.valueOf(50.5));
        PutMoneyResponse pmr = new PutMoneyResponse();

        lenient().when(httpClient.sendPutWithJsonResponse(any(),any(),eq(PutMoneyResponse.class)))
                .thenReturn(pmr);

        assertEquals(pmr, starlingClient.putMoneyToSavingsGoal("test","test",amm));
    }
}
