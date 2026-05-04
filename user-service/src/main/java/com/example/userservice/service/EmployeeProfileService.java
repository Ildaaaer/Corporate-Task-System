package com.example.userservice.service;

import com.example.events.user.UserRegisteredEvent;
import com.example.userservice.dto.EmployeeProfileResponse;
import com.example.userservice.dto.UpdateEmployeeProfileRequest;
import com.example.userservice.Entity.EmployeeProfile;
import com.example.userservice.Entity.Status;
import com.example.userservice.exception.BadRequestException;
import com.example.userservice.exception.NotFoundException;
import com.example.userservice.mapper.EmployeeProfileMapper;
import com.example.userservice.repository.EmployeeProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EmployeeProfileService {

    private final EmployeeProfileRepository employeeProfileRepository;
    private final EmployeeProfileMapper employeeProfileMapper;

    @Transactional
    public EmployeeProfileResponse createProfileFromRegistration(UserRegisteredEvent event) {
        validateRegistrationEvent(event);

        return employeeProfileRepository.findByAuthUserId(event.userId())
                .map(employeeProfileMapper::toResponse)
                .orElseGet(() -> createNewProfileFromEvent(event));
    }

    public EmployeeProfileResponse getByAuthUserId(Long authUserId) {
        EmployeeProfile profile = getProfileEntityByAuthUserId(authUserId);
        return employeeProfileMapper.toResponse(profile);
    }

    public List<EmployeeProfileResponse> getAll() {
        return employeeProfileRepository.findAll()
                .stream()
                .map(employeeProfileMapper::toResponse)
                .toList();
    }

    public List<EmployeeProfileResponse> getByDepartmentId(Long departmentId) {
        validateId(departmentId, "Department id");

        return employeeProfileRepository.findByDepartmentId(departmentId)
                .stream()
                .map(employeeProfileMapper::toResponse)
                .toList();
    }

    @Transactional
    public EmployeeProfileResponse updateProfile(
            Long authUserId,
            UpdateEmployeeProfileRequest request
    ) {
        if (request == null) {
            throw new BadRequestException("Update request must not be null");
        }

        EmployeeProfile profile = getProfileEntityByAuthUserId(authUserId);

        applyProfileUpdates(profile, request);

        EmployeeProfile savedProfile = employeeProfileRepository.save(profile);
        return employeeProfileMapper.toResponse(savedProfile);
    }

    private EmployeeProfileResponse createNewProfileFromEvent(UserRegisteredEvent event) {
        EmployeeProfile profile = EmployeeProfile.builder()
                .authUserId(event.userId())
                .username(event.username().trim())
                .email(event.email().trim().toLowerCase())
                .status(Status.ACTIVE)
                .build();

        EmployeeProfile savedProfile = employeeProfileRepository.save(profile);
        return employeeProfileMapper.toResponse(savedProfile);
    }

    private EmployeeProfile getProfileEntityByAuthUserId(Long authUserId) {
        validateId(authUserId, "Auth user id");

        return employeeProfileRepository.findByAuthUserId(authUserId)
                .orElseThrow(() -> new NotFoundException(
                        "Employee profile with auth user id %d not found".formatted(authUserId)
                ));
    }

    private void applyProfileUpdates(
            EmployeeProfile profile,
            UpdateEmployeeProfileRequest request
    ) {
        if (request.getFirstName() != null) {
            profile.setFirstName(request.getFirstName().trim());
        }

        if (request.getLastName() != null) {
            profile.setLastName(request.getLastName().trim());
        }

        if (request.getDepartmentId() != null) {
            profile.setDepartmentId(request.getDepartmentId());
        }

        if (request.getPosition() != null) {
            profile.setPosition(request.getPosition().trim());
        }

        if (request.getManagerId() != null) {
            profile.setManagerId(request.getManagerId());
        }

        if (request.getStatus() != null) {
            profile.setStatus(request.getStatus());
        }
    }

    private void validateRegistrationEvent(UserRegisteredEvent event) {
        if (event == null) {
            throw new BadRequestException("User registered event must not be null");
        }

        validateId(event.userId(), "Auth user id");

        if (event.username() == null || event.username().isBlank()) {
            throw new BadRequestException("Username must not be blank");
        }

        if (event.email() == null || event.email().isBlank()) {
            throw new BadRequestException("Email must not be blank");
        }
    }

    private void validateId(Long id, String fieldName) {
        if (id == null || id <= 0) {
            throw new BadRequestException(fieldName + " must be positive");
        }
    }
    public List<EmployeeProfileResponse> getByManagerId(Long managerId) {
        validateId(managerId, "Manager id");

        return employeeProfileRepository.findByManagerId(managerId)
                .stream()
                .map(employeeProfileMapper::toResponse)
                .toList();
    }
}
