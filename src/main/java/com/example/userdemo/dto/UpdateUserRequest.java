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
public class UpdateUserRequest {

    @Size(
        min = 2,
        max = 50,
        message = "First name must be between 2 and 50 characters"
    )
    private String firstName;

    @Size(
        min = 2,
        max = 50,
        message = "Last name must be between 2 and 50 characters"
    )
    private String lastName;

    @Size(
        min = 10,
        max = 15,
        message = "Phone number must be between 10 and 15 characters"
    )
    private String phone;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    private String address;

}