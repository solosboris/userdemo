package com.example.userdemo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long id;
    
    @NotBlank(message = "First name is required")
    @Size(
        min = 2,
        max = 50,
        message = "First name must be between 2 and 50 characters"
    )
    private String firstName;
    
    @NotBlank(message = "Last name is required")
    @Size(
        min = 2,
        max = 50,
        message = "Last name must be between 2 and 50 characters"
    )
    private String lastName;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;
    
    @Size(
        min = 10,
        max = 15,
        message = "Phone number must be between 10 and 15 characters"
    )
    private String phone;
    
    private String address;
    @NotBlank(message = "Status is required")
    private String status;
    private String createdAt;
    private String updatedAt;

}