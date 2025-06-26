package com.demo.banking.service;

import com.demo.banking.config.RabbitMQConfig;
import com.demo.banking.model.BalanceChangeNotification;
import com.demo.banking.model.Client;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class NotificationService {
    private final RabbitTemplate rabbitTemplate;

    public NotificationService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * Sends an asynchronous notification about client balance changes to FISC
     *
     * @param client The client whose balance has changed
     * @param previousRonBalance Previous RON account balance
     * @param previousEuroBalance Previous EUR account balance
     */
    public void notifyBalanceChange(Client client, BigDecimal previousRonBalance, BigDecimal previousEuroBalance) {
        // Check which account balance has changed
        boolean ronChanged = !previousRonBalance.equals(client.getRonAccount().getBalance());
        boolean euroChanged = !previousEuroBalance.equals(client.getEuroAccount().getBalance());

        // Only send notification if at least one balance has changed
        if (ronChanged || euroChanged) {
            BalanceChangeNotification notification = new BalanceChangeNotification(client, ronChanged, euroChanged);

            // Send the notification asynchronously to the queue
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.EXCHANGE_NAME,
                    RabbitMQConfig.ROUTING_KEY,
                    notification
            );

            System.out.println("BANK: Sent balance change notification for client with CNP: " + client.getCnp());
        }
    }
}

