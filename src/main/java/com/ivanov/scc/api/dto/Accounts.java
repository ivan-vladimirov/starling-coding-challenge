package com.ivanov.scc.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ivanov.scc.model.Account;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Accounts {
        @JsonProperty("accounts")
        private List<Account> accounts;

        public List<Account> getAccounts() {
                return accounts;
        }

        public void setAccounts(List<Account> accounts) {
                this.accounts = accounts;
        }
}
