package com.example.userservice.Entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "users")
public class EmployeeProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "auth_user_id", unique = true)
    private Long authUserId;

    @Column(name = "username", unique = true,  nullable = false, length = 50)
    private String username;

    @Column(name = "email", nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "first_name", length = 25)
    private String firstName;

    @Column(name = "last_name", length = 50)
    private String lastName;

    @Column(name = "department_id")
    private Long departmentId;

    @Column(name = "position")
    private String position;

    @Column(name = "manager_id")
    private Long managerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status",  nullable = false)
    private Status status;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
