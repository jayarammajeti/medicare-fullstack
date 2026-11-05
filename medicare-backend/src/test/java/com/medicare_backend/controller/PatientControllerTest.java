
package com.medicare_backend.controller;

import com.medicare_backend.dto.PatientRequest;
import com.medicare_backend.dto.PatientResponse;
import com.medicare_backend.service.PatientService;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PatientController.class)
class PatientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PatientService patientService;

    @Test
    void shouldCreatePatient() throws Exception {

        PatientResponse response = new PatientResponse(
                1L,
                "Alex",
                "alex@example.com",
                28
        );

        when(patientService.createPatient(any(PatientRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                      "name": "Alex",
                      "email": "alex@example.com",
                      "age": 28
                    }
                    """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Alex"))
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void shouldRejectInvalidPatient() throws Exception {

        mockMvc.perform(post("/api/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                      "name": "",
                      "email": "bad-email",
                      "age": -5
                    }
                    """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Validation failed"))
                .andExpect(jsonPath("$.fieldErrors.name").exists())
                .andExpect(jsonPath("$.fieldErrors.email").exists())
                .andExpect(jsonPath("$.fieldErrors.age").exists());
    }

    @Test
    void shouldReturn404ForMissingPatient() throws Exception {

        when(patientService.getPatientById(999L))
                .thenThrow(new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Patient not found"
                ));

        mockMvc.perform(get("/api/patients/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message")
                        .value("Patient not found"));
    }
}
