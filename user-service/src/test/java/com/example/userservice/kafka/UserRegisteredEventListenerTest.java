package com.example.userservice.kafka;

import com.example.events.user.UserRegisteredEvent;
import com.example.userservice.service.EmployeeProfileService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserRegisteredEventListenerTest {

    @Mock
    private EmployeeProfileService employeeProfileService;

    @InjectMocks
    private UserRegisteredEventListener userRegisteredEventListener;

    @Test
    void handle_shouldDelegateEventToEmployeeProfileService() {
        UserRegisteredEvent event = new UserRegisteredEvent(
                1L,
                "danil",
                "danil@mail.com",
                "EMPLOYEE",
                Instant.now()
        );

        userRegisteredEventListener.handle(event);

        verify(employeeProfileService).createProfileFromRegistration(event);
    }
}
