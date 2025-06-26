package com.demo.banking.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "clients")
public class Client {
    @Id
    private String cnp;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "ron_account_id")
    private Account ronAccount;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "euro_account_id")
    private Account euroAccount;

    private boolean isMonitored;

    // Default constructor required by JPA
    public Client() {
    }

    // Constructor with all fields
    public Client(String cnp, Account ronAccount, Account euroAccount, boolean isMonitored) {
        this.cnp = cnp;
        this.ronAccount = ronAccount;
        this.euroAccount = euroAccount;
        this.isMonitored = isMonitored;
    }

    // Constructor with cnp
    public Client(String cnp) {
        this.cnp = cnp;
        this.ronAccount = new Account(Currency.RON, BigDecimal.ZERO);
        this.euroAccount = new Account(Currency.EUR, BigDecimal.ZERO);
        this.isMonitored = false;
    }

    // Getters and setters
    public String getCnp() {
        return cnp;
    }

    public void setCnp(String cnp) {
        this.cnp = cnp;
    }

    public Account getRonAccount() {
        return ronAccount;
    }

    public void setRonAccount(Account ronAccount) {
        this.ronAccount = ronAccount;
    }

    public Account getEuroAccount() {
        return euroAccount;
    }

    public void setEuroAccount(Account euroAccount) {
        this.euroAccount = euroAccount;
    }

    public boolean isMonitored() {
        return isMonitored;
    }

    public void setMonitored(boolean monitored) {
        isMonitored = monitored;
    }

    // equals and hashCode methods
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Client client = (Client) o;
        return isMonitored == client.isMonitored &&
                Objects.equals(cnp, client.cnp) &&
                Objects.equals(ronAccount, client.ronAccount) &&
                Objects.equals(euroAccount, client.euroAccount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cnp, ronAccount, euroAccount, isMonitored);
    }

    // toString method
    @Override
    public String toString() {
        return "Client{" +
                "cnp='" + cnp + '\'' +
                ", ronAccount=" + ronAccount +
                ", euroAccount=" + euroAccount +
                ", isMonitored=" + isMonitored +
                '}';
    }
}
