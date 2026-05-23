package medicalcabinet.services;

public interface INotificationChannel {
    void sendNotification(String destination, String message);
}
