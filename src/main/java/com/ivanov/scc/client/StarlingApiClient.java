package com.ivanov.scc.client;

import com.ivanov.scc.exception.HttpNoOkResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class StarlingApiClient {

    private final HttpClient httpClient;

    @Autowired
    public StarlingApiClient(@Qualifier("starling-api") HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public <T> T get(String url, Class<T> responseType) throws HttpNoOkResponse {
        return httpClient.sendGetWithJsonResponse(url, responseType);
    }

    public <T> T put(String url, Object request, Class<T> responseType) {
        return httpClient.sendPutWithJsonResponse(url, request, responseType);
    }
}
