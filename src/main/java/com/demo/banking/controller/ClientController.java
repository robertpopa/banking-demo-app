package com.demo.banking.controller;

import com.demo.banking.model.Client;
import com.demo.banking.model.Currency;
import com.demo.banking.model.ErrorResponse;
import com.demo.banking.service.BankService;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.support.ErrorMessage;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/clients")
public class ClientController {
    private final BankService bankService;

    public ClientController(BankService bankService) {
        this.bankService = bankService;
    }

    @PostMapping("/{cnp}")
    public ResponseEntity<Client> createAccounts(@PathVariable String cnp) {
        return ResponseEntity.ok(bankService.createAccounts(cnp));
    }

    @DeleteMapping("/{cnp}")
    public ResponseEntity<?> closeAccounts(@PathVariable String cnp) {
        try {
            bankService.closeAccounts(cnp);
            return ResponseEntity.ok().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("Closing your account failed: " + e.getMessage()));
        }
    }

    @GetMapping("/{cnp}")
    public ResponseEntity<Client> getAccountInfo(@PathVariable String cnp) {
        return ResponseEntity.ok(bankService.getAccountInfo(cnp));
    }

    @PostMapping("/{cnp}/deposit")
    public ResponseEntity<Void> deposit(
            @PathVariable String cnp,
            @RequestParam Currency currency,
            @RequestParam BigDecimal amount) {
        bankService.deposit(cnp, currency, amount);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{cnp}/withdraw")
    public ResponseEntity<?> withdraw(
            @PathVariable String cnp,
            @RequestParam Currency currency,
            @RequestParam BigDecimal amount) {
        try {
            bankService.withdraw(cnp, currency, amount);
            return ResponseEntity.ok().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("Withdraw operation failed: " + e.getMessage()));
        }
    }
}
