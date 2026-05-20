package com.company.employeemanagement.controller;

import com.company.employeemanagement.dto.request.SalaryRequest;
import com.company.employeemanagement.dto.response.SalaryResponse;
import com.company.employeemanagement.exception.GlobalExceptionHandler;
import com.company.employeemanagement.exception.ResourceNotFoundException;
import com.company.employeemanagement.service.SalaryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class SalaryControllerTest {

    @Mock
    private SalaryService salaryService;

    @InjectMocks
    private SalaryController salaryController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private SalaryResponse salaryResponse;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(salaryController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        objectMapper = new ObjectMapper();

        salaryResponse = SalaryResponse.builder()
                .empNo(10001)
                .firstName("John")
                .lastName("Doe")
                .salary(90000)
                .build();
    }

    @Test
    @DisplayName("PUT /api/v1/salaries/{empNo} - Should update salary")
    void updateSalary_Returns200() throws Exception {
        SalaryRequest request = SalaryRequest.builder().salary(90000).build();
        when(salaryService.updateSalary(eq(10001), any(SalaryRequest.class))).thenReturn(salaryResponse);

        mockMvc.perform(put("/api/v1/salaries/10001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.salary").value(90000))
                .andExpect(jsonPath("$.data.empNo").value(10001));
    }

    @Test
    @DisplayName("GET /api/v1/salaries/{empNo} - Should return salary")
    void getSalary_Returns200() throws Exception {
        when(salaryService.getSalaryByEmployee(10001)).thenReturn(salaryResponse);

        mockMvc.perform(get("/api/v1/salaries/10001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.salary").value(90000))
                .andExpect(jsonPath("$.data.firstName").value("John"));
    }

    @Test
    @DisplayName("GET /api/v1/salaries/{empNo} - Should return 404 when not found")
    void getSalary_NotFound_Returns404() throws Exception {
        when(salaryService.getSalaryByEmployee(99999))
                .thenThrow(new ResourceNotFoundException("Salary", "empNo", 99999));

        mockMvc.perform(get("/api/v1/salaries/99999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PUT /api/v1/salaries/{empNo} - Should return 400 for negative salary")
    void updateSalary_NegativeSalary_Returns400() throws Exception {
        SalaryRequest invalid = SalaryRequest.builder().salary(-1000).build();

        mockMvc.perform(put("/api/v1/salaries/10001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }
}
