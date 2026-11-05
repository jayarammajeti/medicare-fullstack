
package com.medicare_backend.dto;

public record PatientCountResponse(
        long count,
        String source
) {
}
