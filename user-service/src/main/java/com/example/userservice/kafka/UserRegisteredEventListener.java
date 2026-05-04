package com.example.userservice.kafka;

import com.example.events.user.UserRegisteredEvent;
import com.example.userservice.service.EmployeeProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserRegisteredEventListener {

    private final EmployeeProfileService employeeProfileService;

    @KafkaListener(
            topics = "${app.kafka.topics.user-registered}",
            groupId = "user-service"
    )
    public void handle(UserRegisteredEvent event) {
        employeeProfileService.createProfileFromRegistration(event);
    }
}
