package com.example.auth.kafka;


import com.example.events.user.UserRegisteredEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthEventKafkaPublisher {

    private final KafkaTemplate<String, UserRegisteredEvent> kafkaTemplate;
    private final KafkaTopicsProperties  kafkaTopicsProperties;

    public void publishUserRegistered(UserRegisteredEvent event) {
        kafkaTemplate.send(kafkaTopicsProperties.getUserRegistered(), event.userId().toString(), event);
    }
}