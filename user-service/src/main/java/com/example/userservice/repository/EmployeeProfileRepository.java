package com.example.userservice.repository;

import com.example.userservice.Entity.EmployeeProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeProfileRepository extends JpaRepository<EmployeeProfile, Long> {
    Optional<EmployeeProfile> findByAuthUserId(Long authUserId);
    boolean existsByAuthUserId(Long authUserId);
    List<EmployeeProfile> findByDepartmentId(Long departmentId);
    List<EmployeeProfile> findByManagerId(Long managerId);

}
