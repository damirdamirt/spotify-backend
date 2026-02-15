package com.spotify.spotifybackend.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender javaMailSender;

    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void sendVerificationEmail(String toEmail, String token) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(toEmail);
        message.setSubject("Spotify clone - registration confirm");

        String link = "http://127.0.0.1:5500/verify.html?token=" + token;

        message.setText("Hello,\n\n" +
                        "Please, confirm your registration with click on link below:\n " +
                        link + "\n\n" +
                        "Thank you, Spotify clone team");
        javaMailSender.send(message);
    }

    public void sendOtpEmail(String toEmail, String otpCode) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Spotify Clone - Your OTP code for login");
        message.setText("Your one-time login code is: " + otpCode +
                "\n\nThis code is valid for 5 minutes.\nDo not reveal this code to anyone!");

        javaMailSender.send(message);
        System.out.println("OTP Email send to: " + toEmail);
    }

    public void sendMagicLoginLink(String toEmail, String token) {
        String link = "http://127.0.0.1:5500/magic-login.html?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Spotify Clone - Your magic link for login");
        message.setText("Click on the link to login without password:\n" + link +
                "\n\nToken: " + token); // Token for Postman
        javaMailSender.send(message);
    }

    public void sendPasswordChangeLink(String toEmail, String token) {
        // Link vodi na CHANGE PASSWORD formu (na frontendu)
        String link = "http://localhost:3000/change-password-form?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Spotify Clone - Password change request");
        message.setText("Password change requested. Click on the link to continue:\n" + link +
                "\n\nToken: " + token); // Token for Postman
        javaMailSender.send(message);
    }
}
