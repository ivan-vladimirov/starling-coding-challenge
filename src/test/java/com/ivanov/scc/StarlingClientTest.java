package com.ivanov.scc;

import com.ivanov.scc.client.StarlingClient;
import com.ivanov.scc.client.response.AccountResponse;
import com.ivanov.scc.client.response.TransactionsResponse;
import com.ivanov.scc.config.HttpClient;
import com.ivanov.scc.config.HttpNoOkResponse;
import com.ivanov.scc.exception.AccountsNotFoundException;
import com.ivanov.scc.model.Account;
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
        lenient().when(httpClient.sendGetWithJsonResponse(uriCaptor.capture(), eq(AccountResponse.class)))
                .thenThrow(new HttpNoOkResponse("REQ", "RESP", HttpStatus.SC_NOT_FOUND));
        AccountsNotFoundException e = assertThrows(AccountsNotFoundException.class, () -> starlingClient.getAllAccounts());
        assertEquals("Accounts not found for current user.", e.getMessage());
    }

    @Test
    void testGetAllAccounts(){
        lenient().when(httpClient.sendGetWithJsonResponse(uriCaptor.capture(), eq(AccountResponse.class)))
                .thenReturn(new AccountResponse());
        AccountResponse accounts = starlingClient.getAllAccounts();

        assertNotNull(accounts);
    }

    @Test
    void testGetTransactions(){
        Account acc = new Account();
        List<Account> accounts = new ArrayList<>();
        accounts.add(acc);

        AccountResponse accountResponse = new AccountResponse();
        accountResponse.setAccounts(accounts);

        lenient().when(httpClient.sendGetWithJsonResponse(eq("/api/v2/accounts"), eq(AccountResponse.class)))
                .thenReturn(accountResponse);
        lenient().when(httpClient.sendGetWithJsonResponse(uriCaptor.capture(), eq(TransactionsResponse.class)))
                .thenReturn(new TransactionsResponse());
        ZonedDateTime lt = ZonedDateTime.now();
        List<TransactionsResponse> transactions = starlingClient.getTransactions(lt);

        assertNotNull(transactions);
    }
}
