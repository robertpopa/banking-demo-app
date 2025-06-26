package com.demo.banking.service;

import com.demo.banking.model.Account;
import com.demo.banking.model.Client;
import com.demo.banking.model.Currency;
import com.demo.banking.repository.AccountRepository;
import com.demo.banking.repository.ClientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@Transactional
public class BankService {
    private final ClientRepository clientRepository;
    private final AccountRepository accountRepository;
    private final FiscService fiscService;
    rivate final NotificationService notificationService;

    public BankService(ClientRepository clientRepository, AccountRepository accountRepository,
                       FiscService fiscService, NotificationService notificationService) {
        this.clientRepository = clientRepository;
        this.accountRepository = accountRepository;
        this.fiscService = fiscService;
        this.notificationService = notificationService;
    }

    // Client operations
    public Client createAccounts(String cnp) {
        if (clientRepository.existsById(cnp)) {
            throw new IllegalArgumentException("Client with CNP " + cnp + " already exists");
        }

        Client client = new Client(cnp);

        // Save accounts first to get their IDs
        client.setRonAccount(accountRepository.save(client.getRonAccount()));
        client.setEuroAccount(accountRepository.save(client.getEuroAccount()));

        // Then save the client with the account references
        return clientRepository.save(client);
    }

    public void closeAccounts(String cnp) {
        Client client = getClient(cnp);

        // Check if both accounts have zero balance
        if (client.getRonAccount().getBalance().compareTo(BigDecimal.ZERO) != 0 || client.getEuroAccount().getBalance().compareTo(BigDecimal.ZERO) != 0) {
            throw new IllegalStateException("Cannot close accounts with non-zero balance");
        }

        // Remove client from monitoring if needed
        if (client.isMonitored()) {
            fiscService.stopMonitoring(cnp);
        }

        // Remove client (cascade will also remove associated accounts)
        clientRepository.deleteById(cnp);
    }

    public void deposit(String cnp, Currency currency, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive");
        }

        Client client = getClient(cnp);
        Account account = getAccountByCurrency(client, currency);

        // Store previous balances for notification
        BigDecimal previousRonBalance = client.getRonAccount().getBalance();
        BigDecimal previousEuroBalance = client.getEuroAccount().getBalance();

        // Update balance
        account.setBalance(account.getBalance().add(amount));

        // Save the updated account
        accountRepository.save(account);

        // Notify tax authority asynchronously if client is monitored
        if (client.isMonitored()) {
            notificationService.notifyBalanceChange(client, previousRonBalance, previousEuroBalance);
        }
    }

    public void withdraw(String cnp, Currency currency, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive");
        }

        Client client = getClient(cnp);
        Account account = getAccountByCurrency(client, currency);

        // Store previous balances for notification
        BigDecimal previousRonBalance = client.getRonAccount().getBalance();
        BigDecimal previousEuroBalance = client.getEuroAccount().getBalance();

        BigDecimal newBalance = account.getBalance().subtract(amount);

        // Check minimum balance requirement
        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalStateException("Account balance cannot go below 0");
        }

        // For normal operations, enforce minimum balance unless preparing for account closure
        if (newBalance.compareTo(BigDecimal.ZERO) > 0 && newBalance.compareTo(Account.MIN_BALANCE) < 0) {
            throw new IllegalStateException("Account balance cannot go below " + Account.MIN_BALANCE + " except for account closure");
        }

        // Update balance
        account.setBalance(newBalance);

        // Save the updated account
        accountRepository.save(account);

        // Notify tax authority asynchronously if client is monitored
        if (client.isMonitored()) {
            notificationService.notifyBalanceChange(client, previousRonBalance, previousEuroBalance);
        }
    }

    /**
     * Special method to prepare accounts for closure by setting balances to zero
     * This bypasses the minimum balance requirement check
     */
    public void prepareAccountsForClosure(String cnp) {
        Client client = getClient(cnp);

        // Store previous balances for notification
        BigDecimal previousRonBalance = client.getRonAccount().getBalance();
        BigDecimal previousEuroBalance = client.getEuroAccount().getBalance();

        client.getRonAccount().setBalance(BigDecimal.ZERO);
        client.getEuroAccount().setBalance(BigDecimal.ZERO);

        // Save the updated accounts
        accountRepository.save(client.getRonAccount());
        accountRepository.save(client.getEuroAccount());

        // Notify tax authority asynchronously if client is monitored
        if (client.isMonitored()) {
            notificationService.notifyBalanceChange(client, previousRonBalance, previousEuroBalance);
        }
    }

    public Client getAccountInfo(String cnp) {
        return getClient(cnp);
    }

    // Tax authority operations
    public void startMonitoring(String cnp) {
        Client client = getClient(cnp);
        client.setMonitored(true);
        fiscService.startMonitoring(client);
    }

    public void stopMonitoring(String cnp) {
        Client client = getClient(cnp);
        client.setMonitored(false);
        fiscService.stopMonitoring(cnp);
    }

    // Helper methods
    private Client getClient(String cnp) {
        return clientRepository.findById(cnp)
                .orElseThrow(() -> new IllegalArgumentException("Client with CNP " + cnp + " not found"));
    }

    private Account getAccountByCurrency(Client client, Currency currency) {
        return currency == Currency.RON ? client.getRonAccount() : client.getEuroAccount();
    }
}

