package com.example.userservice.dto;

import com.example.userservice.Entity.Status;
import lombok.Data;

@Data
public class UpdateEmployeeProfileRequest {
    private String firstName;
    private String lastName;
    private Long departmentId;
    private String position;
    private Long managerId;
    private Status status;

}
