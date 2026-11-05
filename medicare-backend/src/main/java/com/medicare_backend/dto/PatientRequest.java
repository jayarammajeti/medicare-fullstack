
package com.medicare_backend.dto;

import jakarta.validation.constraints.*;

public record PatientRequest(

        @NotBlank(message = "Name is required")
        @Size(min = 2, max = 80,
                message = "Name must be between 2 and 80 characters")
        String name,

        @NotBlank(message = "Email is required")
        @Email(message = "Enter a valid email address")
        String email,

        @NotNull(message = "Age is required")
        @Min(value = 0, message = "Age cannot be negative")
        @Max(value = 120, message = "Age cannot exceed 120")
        Integer age

) {
}

