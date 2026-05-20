package medicalcabinet.services;

// 1. Interfața (la nivel de pachet, fără 'public')
interface INotificationChannel {
    void sendNotification(String emailOrPhone, String message);
}

// 2. Serviciul de Email (la nivel de pachet, fără 'public')
class EmailNotificationService implements INotificationChannel {
    @Override
    public void sendNotification(String destination, String message) {
        System.out.println("[EMAIL SENT to " + destination + "]: " + message);
    }
}

// 3. Serviciul de SMS (la nivel de pachet, fără 'public')
class SmsNotificationService implements INotificationChannel {
    @Override
    public void sendNotification(String destination, String message) {
        System.out.println("[SMS SENT to " + destination + "]: " + message);
    }
}

public class UserNotificationManager {
    private final INotificationChannel emailService = new EmailNotificationService();
    private final INotificationChannel smsService = new SmsNotificationService();

    public void notifyUserCredentialsChanged(String username, String userEmail, String changeDetails) {
        String message = "Alertă Securitate! Datele de autentificare pentru contul '" + username +
                "' au fost modificate. Detalii: " + changeDetails;

        // Se trimite pe ambele canale simultan (Cerința: cel puțin 2 variante)
        emailService.sendNotification(userEmail, message);
        smsService.sendNotification("0722XXXXXX", message); // Simulare SMS
    }
}