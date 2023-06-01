package com.eliteworks.eliteworks.config;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.util.Properties;

public class EmailSender {
    public static void sendPasswordResetEmail(String recipientEmail, String resetLink) {
        // Email configuration
        String senderEmail = "your_sender_email@example.com";
        String senderPassword = "your_sender_password";
        String emailSubject = "Password Reset";

        // Mailtrap SMTP server configuration
        String smtpHost = "sandbox.smtp.mailtrap.io";
        String smtpPort = "2525";
        String smtpUsername = "504de8eefd8960";
        String smtpPassword = "4d5181f27299de";

        // Create properties for the SMTP server
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", smtpHost);
        properties.put("mail.smtp.port", smtpPort);

        // Create session and authenticate with the SMTP server
        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(smtpUsername, smtpPassword);
            }
        });

        try {
            // Create a new email message
            Message message = new MimeMessage(session);

            // Set the sender and recipient addresses
            message.setFrom(new InternetAddress(senderEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));

            // Set the email subject
            message.setSubject(emailSubject);

            // Set the email content using HTML
            String emailContent = getEmailTemplate(resetLink);
            message.setContent(emailContent, "text/html");

            // Send the email
            Transport.send(message);

            System.out.println("Email sent successfully!");
        } catch (MessagingException e) {
            System.out.println("Error sending email: " + e.getMessage());
        }
    }

    private static String getEmailTemplate(String resetLink) {
        // HTML template for the email
        String template = "<!DOCTYPE html>"
                + "<html>"
                + "<head>"
                + "    <style>"
                + "        body {"
                + "            font-family: Arial, sans-serif;"
                + "        }"
                + "        .container {"
                + "            max-width: 600px;"
                + "            margin: 0 auto;"
                + "            padding: 20px;"
                + "            border: 1px solid #ddd;"
                + "            border-radius: 5px;"
                + "        }"
                + "        h2 {"
                + "            margin-top: 0;"
                + "        }"
                + "        p {"
                + "            margin-bottom: 20px;"
                + "        }"
                + "        .button {"
                + "            display: inline-block;"
                + "            padding: 10px 20px;"
                + "            background-color: #4CAF50;"
                + "            color: #fff;"
                + "            text-decoration: none;"
                + "            border-radius: 5px;"
                + "        }"
                + "    </style>"
                + "</head>"
                + "<body>"
                + "    <div class=\"container\">"
                + "        <h2>Password Reset</h2>"
                + "        <p>You have requested to reset your password. Please click the button below to proceed:</p>"
                + "        <a class=\"button\" href=\"" + resetLink + "\">Reset Password</a>"
                + "    </div>"
                + "</body>"
                + "</html>";

        return template;
    }
}
