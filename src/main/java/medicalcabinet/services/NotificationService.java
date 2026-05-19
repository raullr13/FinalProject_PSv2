package medicalcabinet.services;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

public class NotificationService {
    // TODO: Replace with your actual email and generated App Password
    private static final String SENDER_EMAIL = "your.email@gmail.com";
    private static final String SENDER_PASSWORD = "your_16_letter_app_password";

    public void sendEmail(String toEmail, String subject, String body) throws Exception {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SENDER_EMAIL, SENDER_PASSWORD);
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(SENDER_EMAIL));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
        message.setSubject(subject);
        message.setText(body);

        Transport.send(message); // Actually sends the email over the internet!
    }

    public void sendSMS(String phoneNumber, String message) {
        // Real SMS requires a paid API like Twilio.
        // We simulate it here to fulfill the architecture requirement.
        System.out.println("--- SIMULATED SMS ---");
        System.out.println("To: " + phoneNumber);
        System.out.println("Message: " + message);
        System.out.println("---------------------");
    }
}