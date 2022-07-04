package com.ivanov.scc;

import com.fasterxml.jackson.core.type.TypeReference;
import com.ivanov.scc.config.HttpClient;

import com.ivanov.scc.model.Account;
import com.ivanov.scc.model.AccountResponse;
import com.ivanov.scc.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TestHttpClient {
    public static String URL = "/v2/api/account";
    AccountService accountService;
    @Mock
    HttpClient httpClient;
    @BeforeEach
    void setup(){
        accountService = new AccountService(httpClient);
    }
    @Test
    void testGetAccount(){
        Account account = new Account();
        account.setAccountUid("TEST1");
        Account account2 = new Account();
        account2.setAccountUid("TEST2");

        when(httpClient.sendGetWithJsonResponse(anyString(), any(TypeReference.class))).thenAnswer(invocationOnMock -> {
            String uri = invocationOnMock.getArgument(0);
            if (uri.startsWith("/v2/api/accounts")){
                return List.of(account,account2);
            }
            return null;
        });
        AccountResponse accounts = accountService.retrieveAccounts();

        assertNotNull(accounts);
    }

}
