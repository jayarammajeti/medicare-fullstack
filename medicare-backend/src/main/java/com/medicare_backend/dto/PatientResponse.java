
package com.medicare_backend.dto;

public record PatientResponse(
        Long id,
        String name,
        String email,
        Integer age
) {
}
