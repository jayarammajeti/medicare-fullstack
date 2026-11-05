
package com.medicare_backend.service;

import com.medicare_backend.dto.PatientCountResponse;
import com.medicare_backend.repository.PatientRepository;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class PatientCountService {

    private static final String CACHE_KEY =
            "medicare:patient-count";

    private final PatientRepository patientRepository;
    private final StringRedisTemplate redisTemplate;

    public PatientCountService(
            PatientRepository patientRepository,
            StringRedisTemplate redisTemplate) {

        this.patientRepository = patientRepository;
        this.redisTemplate = redisTemplate;
    }

    public PatientCountResponse getPatientCount() {

        // STEP 1: Check Redis first
        String cachedValue =
                redisTemplate.opsForValue().get(CACHE_KEY);

        // STEP 2: If found, return it immediately
        if (cachedValue != null) {

            return new PatientCountResponse(
                    Long.parseLong(cachedValue),
                    "REDIS"
            );
        }

        // STEP 3: If missing, read PostgreSQL
        long count = patientRepository.count();

        // STEP 4: Store the result in Redis for 30 seconds
        redisTemplate.opsForValue().set(
                CACHE_KEY,
                Long.toString(count),
                Duration.ofSeconds(30)
        );

        // STEP 5: Return the database result
        return new PatientCountResponse(
                count,
                "POSTGRESQL"
        );
    }
}
