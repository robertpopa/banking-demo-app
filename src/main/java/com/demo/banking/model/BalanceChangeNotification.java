package com.demo.banking.model;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Message model for balance change notifications sent from Bank to FISC
 */
public class BalanceChangeNotification implements Serializable {
    private String cnp;
    private BigDecimal ronBalance;
    private BigDecimal euroBalance;
    private boolean ronChanged;
    private boolean euroChanged;

    // Default constructor required for Jackson deserialization
    public BalanceChangeNotification() {
    }

    public BalanceChangeNotification(Client client, boolean ronChanged, boolean euroChanged) {
        this.cnp = client.getCnp();
        this.ronBalance = client.getRonAccount().getBalance();
        this.euroBalance = client.getEuroAccount().getBalance();
        this.ronChanged = ronChanged;
        this.euroChanged = euroChanged;
    }

    public String getCnp() {
        return cnp;
    }

    public void setCnp(String cnp) {
        this.cnp = cnp;
    }

    public BigDecimal getRonBalance() {
        return ronBalance;
    }

    public void setRonBalance(BigDecimal ronBalance) {
        this.ronBalance = ronBalance;
    }

    public BigDecimal getEuroBalance() {
        return euroBalance;
    }

    public void setEuroBalance(BigDecimal euroBalance) {
        this.euroBalance = euroBalance;
    }

    public boolean isRonChanged() {
        return ronChanged;
    }

    public void setRonChanged(boolean ronChanged) {
        this.ronChanged = ronChanged;
    }

    public boolean isEuroChanged() {
        return euroChanged;
    }

    public void setEuroChanged(boolean euroChanged) {
        this.euroChanged = euroChanged;
    }
}

