package com.ivanov.scc.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.ivanov.scc.config.HttpClient;
import com.ivanov.scc.model.Account;
import com.ivanov.scc.model.AccountResponse;
import org.apache.juli.logging.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountService {
    private static final Logger LOG = LoggerFactory.getLogger(AccountService.class);
    private final static String URL = "/api/v2/accounts";
    private final HttpClient httpClient;
    @Autowired
    public AccountService(@Qualifier("starling-api") HttpClient httpClient){
        this.httpClient = httpClient;
    }

    public AccountResponse retrieveAccounts(){
        AccountResponse response = httpClient.sendGetWithJsonResponse(URL, new TypeReference<>() {});
        if(response==null){
            LOG.error("Could not find any accounts.");
            return null;
        }
        return response;
    }
}
