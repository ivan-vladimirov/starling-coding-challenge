package com.ivanov.codingtest.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CodingTestService {
    private static final Logger LOG = LoggerFactory.getLogger(CodingTestService.class);
    @Autowired
    public CodingTestService(){

    }

}
