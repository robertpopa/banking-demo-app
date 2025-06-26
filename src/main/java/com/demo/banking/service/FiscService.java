package com.demo.banking.service;

import com.demo.banking.config.RabbitMQConfig;
import com.demo.banking.model.BalanceChangeNotification;
import com.demo.banking.model.Client;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
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

    /**
     * Asynchronously receive balance change notifications from the message queue
     */
    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public void handleBalanceChangeNotification(BalanceChangeNotification notification) {
        String cnp = notification.getCnp();
        Client cachedClient = monitoredClients.get(cnp);

        // If client is not monitored, ignore the notification
        if (cachedClient == null) {
            System.out.println("FISC: Ignoring notification for non-monitored client with CNP: " + cnp);
            return;
        }

        // Create updated client from notification data
        Client updatedClient = copyClient(cachedClient);

        // Update cached client information and display appropriate messages
        System.out.println("FISC: Async notification received for client with CNP: " + cnp);

        if (notification.isRonChanged()) {
            updatedClient.getRonAccount().setBalance(notification.getRonBalance());
            System.out.println("FISC: RON account balance changed to: " + notification.getRonBalance());
        }

        if (notification.isEuroChanged()) {
            updatedClient.getEuroAccount().setBalance(notification.getEuroBalance());
            System.out.println("FISC: EUR account balance changed to: " + notification.getEuroBalance());
        }

        // Update cached client information
        monitoredClients.put(cnp, updatedClient);
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

