package com.demo.banking;

import com.demo.banking.model.BalanceChangeNotification;
import com.demo.banking.model.Client;
import com.demo.banking.service.FiscService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class FiscServiceTest {

    @InjectMocks
    private FiscService fiscService;

    private Client testClient;
    private BalanceChangeNotification notification;

    @BeforeEach
    void setUp() {
        // Create a test client
        testClient = new Client("1234567890123");
        testClient.getRonAccount().setBalance(new BigDecimal("2000.0"));
        testClient.getEuroAccount().setBalance(new BigDecimal("1000.0"));
        testClient.setMonitored(true);

        // Create a test notification
        notification = new BalanceChangeNotification();
        notification.setCnp("1234567890123");
        notification.setRonBalance(new BigDecimal("2500.0"));
        notification.setEuroBalance(new BigDecimal("1000.0"));
        notification.setRonChanged(true);
        notification.setEuroChanged(false);
    }

    @Test
    void shouldStartAndStopMonitoringClient() {
        // Start monitoring
        fiscService.startMonitoring(testClient);

        // Verify client is monitored
        Client cachedClient = getMonitoredClient("1234567890123");
        assertNotNull(cachedClient);
        assertEquals("1234567890123", cachedClient.getCnp());
        assertEquals(new BigDecimal("2000.0"), cachedClient.getRonAccount().getBalance());

        // Stop monitoring
        fiscService.stopMonitoring("1234567890123");

        // Verify client is no longer monitored
        assertNull(getMonitoredClient("1234567890123"));
    }

    /**
     * Helper method to access the private monitoredClients map in FiscService
     */
    @SuppressWarnings("unchecked")
    private Client getMonitoredClient(String cnp) {
        try {
            Field monitoredClientsField = FiscService.class.getDeclaredField("monitoredClients");
            monitoredClientsField.setAccessible(true);
            Map<String, Client> monitoredClients = (Map<String, Client>) monitoredClientsField.get(fiscService);
            return monitoredClients.get(cnp);
        } catch (Exception e) {
            fail("Failed to access monitoredClients field: " + e.getMessage());
            return null;
        }
    }
}

