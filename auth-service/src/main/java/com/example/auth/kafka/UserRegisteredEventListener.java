package com.example.auth.kafka;


import com.example.events.user.UserRegisteredEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class UserRegisteredEventListener {

    private final AuthEventKafkaPublisher authEventKafkaPublisher;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(UserRegisteredEvent event) {
        authEventKafkaPublisher.publishUserRegistered(event);
    }
}
