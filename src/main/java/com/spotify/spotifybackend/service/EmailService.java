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

        String link = "http://localhost:8080/api/auth/verify?token=" + token;

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
}
