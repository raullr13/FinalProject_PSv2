package com.medicalcabinet.backend.services;

import medicalcabinet.services.INotificationChannel;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UserNotificationManager {
    private final List<INotificationChannel> channels;

    public UserNotificationManager(List<INotificationChannel> channels) {
        this.channels = channels;
    }

    public void notifyUserCredentialsChanged(String username, String userEmail, String changeDetails) {
        String message = "Alertă Securitate! Datele de autentificare pentru contul '" + username +
                "' au fost modificate. Detalii: " + changeDetails;

        for (INotificationChannel channel : channels) {
            channel.sendNotification(userEmail, message);
        }
    }
}