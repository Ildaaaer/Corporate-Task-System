package com.example.userservice.mapper;

import com.example.userservice.Entity.EmployeeProfile;
import com.example.userservice.dto.EmployeeProfileResponse;
import org.springframework.stereotype.Component;

@Component
public class EmployeeProfileMapper {
    public EmployeeProfileResponse toResponse(EmployeeProfile profile) {
        return EmployeeProfileResponse.builder()
                .id(profile.getId())
                .authUserId(profile.getAuthUserId())
                .username(profile.getUsername())
                .email(profile.getEmail())
                .firstName(profile.getFirstName())
                .lastName(profile.getLastName())
                .departmentId(profile.getDepartmentId())
                .position(profile.getPosition())
                .managerId(profile.getManagerId())
                .status(profile.getStatus().name())
                .build();
    }
}
