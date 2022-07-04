package com.ivanov.scc.client.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ivanov.scc.model.Account;

import java.util.List;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AccountResponse {
        @JsonProperty("accounts")
        private List<Account> accounts;

        public List<Account> getAccounts() {
                return accounts;
        }

        public void setAccounts(List<Account> accounts) {
                this.accounts = accounts;
        }

        @Override
        public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;
                AccountResponse that = (AccountResponse) o;
                return Objects.equals(accounts, that.accounts);
        }

        @Override
        public int hashCode() {
                return Objects.hash(accounts);
        }

        @Override
        public String toString() {
                return "AccountResponse{" +
                        "accounts=" + accounts +
                        '}';
        }
}
