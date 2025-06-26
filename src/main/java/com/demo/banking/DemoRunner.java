package com.demo.banking;

import com.demo.banking.model.Currency;
import com.demo.banking.service.BankService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DemoRunner implements CommandLineRunner {
    private final BankService bankService;

    public DemoRunner(BankService bankService) {
        this.bankService = bankService;
    }

    @Override
    public void run(String... args) {
        System.out.println("=== BANK DEMO APPLICATION ===");

        // Create a client
        String cnp = "1234567890123";
        System.out.println("\n1. Creating accounts for client with CNP: " + cnp);
        bankService.createAccounts(cnp);

        // Initial deposit to meet minimum balance requirements
        System.out.println("\n2. Initial deposit to meet minimum balance requirements");
        bankService.deposit(cnp, Currency.RON, new BigDecimal("2000.00"));
        bankService.deposit(cnp, Currency.EUR, new BigDecimal("2000.00"));

        // Check account info
        System.out.println("\n3. Account information after initial deposit:");
        System.out.println(bankService.getAccountInfo(cnp));

        // Start monitoring by FISC
        System.out.println("\n4. FISC starts monitoring the client");
        bankService.startMonitoring(cnp);

        // Make some transactions
        System.out.println("\n5. Client makes a deposit of 500 RON");
        bankService.deposit(cnp, Currency.RON, new BigDecimal("500.00"));

        System.out.println("\n6. Client withdraws 300 EUR");
        bankService.withdraw(cnp, Currency.EUR, new BigDecimal("300.00"));

        // Check account info again
        System.out.println("\n7. Account information after transactions:");
        System.out.println(bankService.getAccountInfo(cnp));

        // Stop monitoring
        System.out.println("\n8. FISC stops monitoring the client");
        bankService.stopMonitoring(cnp);

        // Make another transaction without monitoring
        System.out.println("\n9. Client makes another deposit (not monitored)");
        bankService.deposit(cnp, Currency.RON, new BigDecimal("1000.00"));

        // Try to close accounts with non-zero balance
        System.out.println("\n10. Attempt to close accounts with non-zero balance:");
        try {
            bankService.closeAccounts(cnp);
        } catch (IllegalStateException e) {
            System.out.println("Error: " + e.getMessage());
        }

        // Withdraw all money
        System.out.println("\n11. Withdraw all money to prepare for account closure");

        // Use the special method to prepare accounts for closure
        System.out.println("\n12. Setting accounts to zero balance for closure");
        bankService.prepareAccountsForClosure(cnp);

        System.out.println("Account balances set to zero for closure");

        // Check account info before closing
        System.out.println("\n13. Account information before closing:");
        System.out.println(bankService.getAccountInfo(cnp));

        // Close accounts successfully
        System.out.println("\n14. Close accounts with zero balance");
        bankService.closeAccounts(cnp);

        System.out.println("\n=== DEMO COMPLETED ===");
    }
}
