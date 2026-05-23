package com.medicalcabinet.backend.services;

import medicalcabinet.services.INotificationChannel;
import org.springframework.stereotype.Service;

@Service
public class EmailNotificationChannel implements INotificationChannel {
    @Override
    public void sendNotification(String destination, String message) {
        System.out.println("[EMAIL SENT to " + destination + "]: " + message);
    }
}