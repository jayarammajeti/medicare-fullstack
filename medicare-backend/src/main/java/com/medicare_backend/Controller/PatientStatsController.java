
package com.medicare_backend.controller;

import com.medicare_backend.dto.PatientCountResponse;
import com.medicare_backend.service.PatientCountService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PatientStatsController {

    private final PatientCountService patientCountService;

    public PatientStatsController(
            PatientCountService patientCountService) {

        this.patientCountService = patientCountService;
    }

    @GetMapping("/api/stats/patient-count")
    public PatientCountResponse getPatientCount() {

        return patientCountService.getPatientCount();
    }
}
