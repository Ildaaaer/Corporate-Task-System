package com.example.userservice.service;

import com.example.events.user.UserRegisteredEvent;
import com.example.userservice.Entity.EmployeeProfile;
import com.example.userservice.Entity.Status;
import com.example.userservice.dto.EmployeeProfileResponse;
import com.example.userservice.dto.UpdateEmployeeProfileRequest;
import com.example.userservice.exception.NotFoundException;
import com.example.userservice.mapper.EmployeeProfileMapper;
import com.example.userservice.repository.EmployeeProfileRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeProfileServiceTest {

    @Mock
    private EmployeeProfileRepository employeeProfileRepository;

    @Mock
    private EmployeeProfileMapper employeeProfileMapper;

    @InjectMocks
    private EmployeeProfileService employeeProfileService;

    @Test
    void createProfileFromRegistration_shouldCreateProfileWhenNotExists() {
        UserRegisteredEvent event = new UserRegisteredEvent(
                1L,
                "danil",
                "DANIL@mail.com",
                "EMPLOYEE",
                Instant.now()
        );

        EmployeeProfileResponse response = EmployeeProfileResponse.builder()
                .id(10L)
                .authUserId(1L)
                .username("danil")
                .email("danil@mail.com")
                .status("ACTIVE")
                .build();

        when(employeeProfileRepository.findByAuthUserId(1L)).thenReturn(Optional.empty());
        when(employeeProfileRepository.save(any(EmployeeProfile.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(employeeProfileMapper.toResponse(any(EmployeeProfile.class))).thenReturn(response);

        EmployeeProfileResponse result = employeeProfileService.createProfileFromRegistration(event);

        assertEquals(1L, result.getAuthUserId());
        assertEquals("danil", result.getUsername());
        assertEquals("danil@mail.com", result.getEmail());

        ArgumentCaptor<EmployeeProfile> profileCaptor = ArgumentCaptor.forClass(EmployeeProfile.class);
        verify(employeeProfileRepository).save(profileCaptor.capture());

        EmployeeProfile savedProfile = profileCaptor.getValue();
        assertEquals(1L, savedProfile.getAuthUserId());
        assertEquals("danil", savedProfile.getUsername());
        assertEquals("danil@mail.com", savedProfile.getEmail());
        assertEquals(Status.ACTIVE, savedProfile.getStatus());
    }

    @Test
    void createProfileFromRegistration_shouldNotCreateDuplicateWhenProfileAlreadyExists() {
        UserRegisteredEvent event = new UserRegisteredEvent(
                1L,
                "danil",
                "danil@mail.com",
                "EMPLOYEE",
                Instant.now()
        );

        EmployeeProfile existingProfile = buildProfile();
        EmployeeProfileResponse response = buildResponse();

        when(employeeProfileRepository.findByAuthUserId(1L)).thenReturn(Optional.of(existingProfile));
        when(employeeProfileMapper.toResponse(existingProfile)).thenReturn(response);

        EmployeeProfileResponse result = employeeProfileService.createProfileFromRegistration(event);

        assertEquals(1L, result.getAuthUserId());
        verify(employeeProfileRepository, never()).save(any());
    }

    @Test
    void getByAuthUserId_shouldThrowNotFoundExceptionWhenProfileDoesNotExist() {
        when(employeeProfileRepository.findByAuthUserId(1L)).thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> employeeProfileService.getByAuthUserId(1L)
        );
    }

    @Test
    void updateProfile_shouldUpdateOnlyProfileFields() {
        EmployeeProfile profile = buildProfile();

        UpdateEmployeeProfileRequest request = new UpdateEmployeeProfileRequest();
        request.setFirstName("Ivan");
        request.setLastName("Petrov");
        request.setDepartmentId(2L);
        request.setPosition("Backend Developer");
        request.setManagerId(5L);
        request.setStatus(Status.ACTIVE);

        EmployeeProfileResponse response = buildResponse();

        when(employeeProfileRepository.findByAuthUserId(1L)).thenReturn(Optional.of(profile));
        when(employeeProfileRepository.save(profile)).thenReturn(profile);
        when(employeeProfileMapper.toResponse(profile)).thenReturn(response);

        employeeProfileService.updateProfile(1L, request);

        assertEquals(1L, profile.getAuthUserId());
        assertEquals("danil", profile.getUsername());
        assertEquals("danil@mail.com", profile.getEmail());

        assertEquals("Ivan", profile.getFirstName());
        assertEquals("Petrov", profile.getLastName());
        assertEquals(2L, profile.getDepartmentId());
        assertEquals("Backend Developer", profile.getPosition());
        assertEquals(5L, profile.getManagerId());
        assertEquals(Status.ACTIVE, profile.getStatus());
    }

    private EmployeeProfile buildProfile() {
        return EmployeeProfile.builder()
                .id(10L)
                .authUserId(1L)
                .username("danil")
                .email("danil@mail.com")
                .status(Status.ACTIVE)
                .build();
    }

    private EmployeeProfileResponse buildResponse() {
        return EmployeeProfileResponse.builder()
                .id(10L)
                .authUserId(1L)
                .username("danil")
                .email("danil@mail.com")
                .status("ACTIVE")
                .build();
    }
}
