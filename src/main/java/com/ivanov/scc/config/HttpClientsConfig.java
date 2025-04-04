package com.ivanov.scc.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ivanov.scc.api.HttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HttpClientsConfig {

    @Bean("starling-api")
    public HttpClient starlingApi(ObjectMapper objectMapper, @Value("${external.starling.base-url}") String url
                                  , @Value("${external.starling.token}") String token){
        return HttpClient.newBuilder()
                .objectMapper(objectMapper)
                .retryCount(3)
                .rootUrl(url)
                .token(token)
                .build("starling-api");
    }
}
