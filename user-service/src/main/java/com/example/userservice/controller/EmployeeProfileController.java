package com.example.userservice.controller;

import com.example.userservice.dto.EmployeeProfileResponse;
import com.example.userservice.dto.UpdateEmployeeProfileRequest;
import com.example.userservice.service.EmployeeProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class EmployeeProfileController {

    private final EmployeeProfileService employeeProfileService;

    @GetMapping("/{authUserId}")
    @ResponseStatus(HttpStatus.OK)
    public EmployeeProfileResponse getByAuthUserId(@PathVariable Long authUserId) {
        return employeeProfileService.getByAuthUserId(authUserId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EmployeeProfileResponse> getAll() {
        return employeeProfileService.getAll();
    }

    @GetMapping("/department/{departmentId}")
    @ResponseStatus(HttpStatus.OK)
    public List<EmployeeProfileResponse> getByDepartmentId(@PathVariable Long departmentId) {
        return employeeProfileService.getByDepartmentId(departmentId);
    }

    @GetMapping("/manager/{managerId}")
    @ResponseStatus(HttpStatus.OK)
    public List<EmployeeProfileResponse> getByManagerId(@PathVariable Long managerId) {
        return employeeProfileService.getByManagerId(managerId);
    }

    @PatchMapping("/{authUserId}")
    @ResponseStatus(HttpStatus.OK)
    public EmployeeProfileResponse updateProfile(
            @PathVariable Long authUserId,
            @Valid @RequestBody UpdateEmployeeProfileRequest request
    ) {
        return employeeProfileService.updateProfile(authUserId, request);
    }
}
