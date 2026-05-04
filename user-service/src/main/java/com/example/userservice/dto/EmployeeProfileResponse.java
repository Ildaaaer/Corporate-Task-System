package com.example.userservice.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmployeeProfileResponse {
    private Long id;
    private Long authUserId;
    private String username;
    private String email;

    private String firstName;
    private String lastName;
    private Long departmentId;
    private String position;
    private Long managerId;
    private String status;
}
