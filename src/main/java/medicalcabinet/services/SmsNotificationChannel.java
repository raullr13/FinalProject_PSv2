package com.medicalcabinet.backend.services;

import medicalcabinet.services.INotificationChannel;
import org.springframework.stereotype.Service;

@Service
public class SmsNotificationChannel implements INotificationChannel {
    @Override
    public void sendNotification(String destination, String message) {
        System.out.println("[SMS SENT to " + destination + "]: " + message);
    }
}