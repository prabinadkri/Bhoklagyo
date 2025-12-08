package com.example.Bhoklagyo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    
    private final JavaMailSender mailSender;

    @Value("${app.invite.accept-base-url:http://localhost:5173/accept-invitation}")
    private String acceptBaseUrl;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendInviteEmail(String to, String token,String restaurantName) {
        String acceptLink = acceptBaseUrl + "?token=" + token;
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("You're invited to join a restaurant as EMPLOYEE");
        message.setText("Hello,\n\nYou have been invited to join " + restaurantName + " as an EMPLOYEE. " +
                "Click the link below to accept the invitation:\n" + acceptLink + "\n\n" +
                "If you did not expect this, you can ignore this email.");
        mailSender.send(message);
    }
}
