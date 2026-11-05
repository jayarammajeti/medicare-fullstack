
package com.medicare_backend.service;

import com.medicare_backend.dto.PatientRequest;
import com.medicare_backend.dto.PatientResponse;
import com.medicare_backend.entity.Patient;
import com.medicare_backend.repository.PatientRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Locale;
import com.medicare_backend.events.PatientRegisteredEvent;
import org.springframework.context.ApplicationEventPublisher;


@Service
public class PatientService {

    private final PatientRepository repository;
    private final ApplicationEventPublisher eventPublisher;

    public PatientService(
            PatientRepository repository,
            ApplicationEventPublisher eventPublisher) {

        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }


    @Transactional
    public PatientResponse createPatient(PatientRequest request) {

        String email = request.email().trim()
                .toLowerCase(Locale.ROOT);

        checkEmail(email, null);

        Patient patient = new Patient(
                request.name().trim(),
                email,
                request.age()
        );

        Patient saved = repository.save(patient);
        eventPublisher.publishEvent(
                new PatientRegisteredEvent(saved.getId())
        );
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<PatientResponse> getAllPatients() {

        return repository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public PatientResponse getPatientById(Long id) {

        return toResponse(findPatient(id));
    }

    @Transactional
    public PatientResponse updatePatient(
            Long id, PatientRequest request) {

        Patient patient = findPatient(id);

        String email = request.email().trim()
                .toLowerCase(Locale.ROOT);

        checkEmail(email, id);

        patient.setName(request.name().trim());
        patient.setEmail(email);
        patient.setAge(request.age());

        return toResponse(repository.save(patient));
    }

    @Transactional
    public void deletePatient(Long id) {

        Patient patient = findPatient(id);

        repository.delete(patient);
    }

    private Patient findPatient(Long id) {

        return repository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Patient not found"
                        )
                );
    }

    private void checkEmail(String email, Long currentId) {

        repository.findByEmailIgnoreCase(email)
                .ifPresent(existing -> {

                    if (!existing.getId().equals(currentId)) {
                        throw new ResponseStatusException(
                                HttpStatus.CONFLICT,
                                "Email already registered"
                        );
                    }
                });
    }

    private PatientResponse toResponse(Patient patient) {

        return new PatientResponse(
                patient.getId(),
                patient.getName(),
                patient.getEmail(),
                patient.getAge()
        );
    }
}
