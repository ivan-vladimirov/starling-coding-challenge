package com.ivanov.scc.model;

import java.time.LocalDate;
import java.util.Objects;

public class Account {
    private String accountUid;
    private AccountType accountType;
    private String defaultCategory;
    private String currency;
    private LocalDate createdAt;
    private String name;

    public String getAccountUid() {
        return accountUid;
    }

    public void setAccountUid(String accountUid) {
        this.accountUid = accountUid;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    public String getDefaultCategory() {
        return defaultCategory;
    }

    public void setDefaultCategory(String defaultCategory) {
        this.defaultCategory = defaultCategory;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return Objects.equals(accountUid, account.accountUid) && accountType == account.accountType && Objects.equals(defaultCategory, account.defaultCategory) && Objects.equals(currency, account.currency) && Objects.equals(createdAt, account.createdAt) && Objects.equals(name, account.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountUid, accountType, defaultCategory, currency, createdAt, name);
    }

    @Override
    public String toString() {
        return "Account{" +
                "accountUid='" + accountUid + '\'' +
                ", accountType=" + accountType +
                ", defaultCategory='" + defaultCategory + '\'' +
                ", currency='" + currency + '\'' +
                ", createdAt=" + createdAt +
                ", name='" + name + '\'' +
                '}';
    }
}
