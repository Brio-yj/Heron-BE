package com.example.hackdemo.event;

import com.example.hackdemo.service.RegistrationEmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class UserRegistrationEventListener {

    private static final Logger logger = LoggerFactory.getLogger(UserRegistrationEventListener.class);

    private final RegistrationEmailService registrationEmailService;

    public UserRegistrationEventListener(RegistrationEmailService registrationEmailService) {
        this.registrationEmailService = registrationEmailService;
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onUserRegistered(UserRegisteredEvent event) {
        logger.info("Handling user registration event for userId={} email={}", event.getUserId(), event.getEmail());
        registrationEmailService.sendRegistrationEmail(event.getEmail(), event.getName());
    }
}
