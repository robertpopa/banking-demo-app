package com.demo.banking;

import com.demo.banking.model.Client;
import com.demo.banking.model.Currency;
import com.demo.banking.repository.AccountRepository;
import com.demo.banking.repository.ClientRepository;
import com.demo.banking.service.BankService;
import com.demo.banking.service.FiscService;
import com.demo.banking.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BankServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private FiscService fiscService;

    @Mock
    private NotificationService notificationService;

    @Captor
    private ArgumentCaptor<BigDecimal> previousRonBalanceCaptor;

    @Captor
    private ArgumentCaptor<BigDecimal> previousEuroBalanceCaptor;

    private BankService bankService;
    private Client testClient;

    @BeforeEach
    void setUp() {
        bankService = new BankService(clientRepository, accountRepository, fiscService, notificationService);

        // Create a test client
        testClient = new Client("1234567890123");
        testClient.getRonAccount().setBalance(new BigDecimal("2000.0"));
        testClient.getEuroAccount().setBalance(new BigDecimal("1000.0"));
        testClient.setMonitored(true);

        // Mock repository behavior
        when(clientRepository.findById("1234567890123")).thenReturn(Optional.of(testClient));
    }

    @Test
    void shouldDepositAndNotifyWhenMonitored() {
        // When
        bankService.deposit("1234567890123", Currency.RON, new BigDecimal("500.0"));

        // Then
        verify(notificationService).notifyBalanceChange(
                eq(testClient),
                previousRonBalanceCaptor.capture(),
                previousEuroBalanceCaptor.capture()
        );

        assertEquals(new BigDecimal("2000.0"), previousRonBalanceCaptor.getValue());
        assertEquals(new BigDecimal("1000.0"), previousEuroBalanceCaptor.getValue());
        assertEquals(new BigDecimal("2500.0"), testClient.getRonAccount().getBalance());
    }

    @Test
    void shouldThrowExceptionWhenWithdrawingBelowMinimumBalance() {
        // When/Then
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            bankService.withdraw("1234567890123", Currency.RON, new BigDecimal("1500.0"));
        });

        assertTrue(exception.getMessage().contains("cannot go below"));
        verifyNoInteractions(notificationService);
    }

    @Test
    void shouldThrowExceptionWhenClosingAccountsWithNonZeroBalance() {
        // Given
        // Client already has non-zero balances from setUp (RON: 2000.0, EUR: 1000.0)

        // When/Then
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            bankService.closeAccounts("1234567890123");
        });

        // Verify exception message
        assertEquals("Cannot close accounts with non-zero balance", exception.getMessage());

        // Verify the client wasn't removed
        verify(clientRepository, never()).deleteById(anyString());

        // Verify FISC monitoring wasn't affected
        verify(fiscService, never()).stopMonitoring(anyString());
    }
}
