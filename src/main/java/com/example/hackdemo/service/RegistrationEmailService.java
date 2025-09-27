package com.example.hackdemo.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Service
public class RegistrationEmailService {

    private static final Logger logger = LoggerFactory.getLogger(RegistrationEmailService.class);

    private final JavaMailSender mailSender;
    private final String fromAddress;

    public RegistrationEmailService(JavaMailSender mailSender,
                                     @Value("${app.mail.registration.from:no-reply@heron.com}") String fromAddress) {
        this.mailSender = mailSender;
        this.fromAddress = fromAddress;
    }

    @Retryable(value = MailException.class, maxAttempts = 5,
            backoff = @Backoff(delay = 2000L, multiplier = 2.0))
    public void sendRegistrationEmail(String recipientEmail, String recipientName) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromAddress);
        message.setTo(recipientEmail);
        message.setSubject("Welcome to Heron!");
        message.setText(String.format("Hello %s,%n%nYour registration was successful. Welcome aboard!", recipientName));

        logger.info("Sending registration email to {}", recipientEmail);
        mailSender.send(message);
    }

    @Recover
    public void recover(MailException exception, String recipientEmail, String recipientName) {
        logger.error("Failed to send registration email to {} after retries", recipientEmail, exception);
    }
}
