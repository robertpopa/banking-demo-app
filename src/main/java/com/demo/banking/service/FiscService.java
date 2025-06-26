package com.demo.banking.service;

import com.demo.banking.model.Client;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class FiscService {
    // Cache to store monitored clients' information
    private final Map<String, Client> monitoredClients = new HashMap<>();

    public void startMonitoring(Client client) {
        // Cache the client information
        monitoredClients.put(client.getCnp(), copyClient(client));
        System.out.println("FISC: Started monitoring client with CNP: " + client.getCnp());
    }

    public void stopMonitoring(String cnp) {
        if (monitoredClients.remove(cnp) != null) {
            System.out.println("FISC: Stopped monitoring client with CNP: " + cnp);
        }
    }

    // Helper method to create a deep copy of a client
    private Client copyClient(Client original) {
        Client copy = new Client(original.getCnp());
        copy.getRonAccount().setBalance(original.getRonAccount().getBalance());
        copy.getEuroAccount().setBalance(original.getEuroAccount().getBalance());
        copy.setMonitored(original.isMonitored());
        return copy;
    }
}

