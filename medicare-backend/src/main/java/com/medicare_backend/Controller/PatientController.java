
package com.medicare_backend.controller;

import com.medicare_backend.dto.PatientRequest;
import com.medicare_backend.dto.PatientResponse;
import com.medicare_backend.service.PatientService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patients")
public class PatientController {

    private final PatientService service;

    public PatientController(PatientService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<PatientResponse> createPatient(
            @Valid @RequestBody PatientRequest request) {

        PatientResponse patient = service.createPatient(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(patient);
    }

    @GetMapping
    public List<PatientResponse> getAllPatients() {

        return service.getAllPatients();
    }

    @GetMapping("/{id}")
    public PatientResponse getPatientById(
            @PathVariable Long id) {

        return service.getPatientById(id);
    }

    @PutMapping("/{id}")
    public PatientResponse updatePatient(
            @PathVariable Long id,
            @Valid @RequestBody PatientRequest request) {

        return service.updatePatient(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePatient(@PathVariable Long id) {

        service.deletePatient(id);
    }
}
