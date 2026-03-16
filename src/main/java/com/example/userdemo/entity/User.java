package com.example.userdemo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "First name is required")
    @Size(
        min = 2,
        max = 50,
        message = "First name must be between 2 and 50 characters"
    )
    @Column(name = "first_name", nullable = false)
    private String firstName;
    
    @NotBlank(message = "Last name is required")
    @Size(
        min = 2,
        max = 50,
        message = "Last name must be between 2 and 50 characters"
    )
    @Column(name = "last_name", nullable = false)
    private String lastName;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Column(unique = true, nullable = false)
    private String email;
    
    @Size(
        min = 10,
        max = 15,
        message = "Phone number must be between 10 and 15 characters"
    )
    private String phone;
    
    private String address;

    @NotBlank(message = "Status is required")
    @Column(name = "status", nullable = false)
    private String status = UserStatus.ACTIVE.toString();
    
    @Column(name = "created_at", updatable = false)
    private Timestamp createdAt;
    
    @Column(name = "updated_at")
    private Timestamp updatedAt;
    
    @PrePersist
    protected void onCreate() {
        Timestamp date = new Timestamp(
            System.currentTimeMillis()
        );
        createdAt = date;
        updatedAt = date;
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = new Timestamp(
            System.currentTimeMillis()
        );
    }

}