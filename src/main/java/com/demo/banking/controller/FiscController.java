package com.demo.banking.controller;

import com.demo.banking.service.BankService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/fisc")
public class FiscController {
    private final BankService bankService;

    public FiscController(BankService bankService) {
        this.bankService = bankService;
    }

    @PostMapping("/monitor/{cnp}")
    public ResponseEntity<Void> startMonitoring(@PathVariable String cnp) {
        bankService.startMonitoring(cnp);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/monitor/{cnp}")
    public ResponseEntity<Void> stopMonitoring(@PathVariable String cnp) {
        bankService.stopMonitoring(cnp);
        return ResponseEntity.ok().build();
    }
}
