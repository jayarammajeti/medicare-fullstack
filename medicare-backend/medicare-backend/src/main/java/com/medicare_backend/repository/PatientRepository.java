

package com.medicare_backend.repository;

import com.medicare_backend.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PatientRepository
        extends JpaRepository<Patient, Long> {

    Optional<Patient> findByEmailIgnoreCase(String email);
}
