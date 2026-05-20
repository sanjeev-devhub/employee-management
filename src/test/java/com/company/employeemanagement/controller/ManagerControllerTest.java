package com.company.employeemanagement.controller;

import com.company.employeemanagement.dto.request.ManagerAssignRequest;
import com.company.employeemanagement.dto.response.ManagerResponse;
import com.company.employeemanagement.exception.GlobalExceptionHandler;
import com.company.employeemanagement.service.ManagerService;
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

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ManagerControllerTest {

    @Mock
    private ManagerService managerService;

    @InjectMocks
    private ManagerController managerController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private ManagerResponse managerResponse;
    private ManagerAssignRequest assignRequest;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(managerController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        objectMapper = new ObjectMapper();

        managerResponse = ManagerResponse.builder()
                .deptNo("D005")
                .deptName("Development")
                .empNo(10001)
                .firstName("John")
                .lastName("Doe")
                .build();

        assignRequest = ManagerAssignRequest.builder()
                .deptNo("D005")
                .empNo(10001)
                .build();
    }

    @Test
    @DisplayName("POST /api/v1/managers/assign - Should assign manager and return 201")
    void assignManager_Returns201() throws Exception {
        when(managerService.assignManager(any(ManagerAssignRequest.class))).thenReturn(managerResponse);

        mockMvc.perform(post("/api/v1/managers/assign")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(assignRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.data.deptNo").value("D005"))
                .andExpect(jsonPath("$.data.empNo").value(10001));
    }

    @Test
    @DisplayName("DELETE /api/v1/managers/remove - Should remove manager")
    void removeManager_Returns200() throws Exception {
        doNothing().when(managerService).removeManager(any(ManagerAssignRequest.class));

        mockMvc.perform(delete("/api/v1/managers/remove")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(assignRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Manager removed successfully"));
    }

    @Test
    @DisplayName("GET /api/v1/managers - Should return all managers")
    void getAllManagers_Returns200() throws Exception {
        when(managerService.getAllManagers()).thenReturn(List.of(managerResponse));

        mockMvc.perform(get("/api/v1/managers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].deptNo").value("D005"))
                .andExpect(jsonPath("$.data[0].firstName").value("John"));
    }

    @Test
    @DisplayName("GET /api/v1/managers/departments/{deptNo} - Should return managers by department")
    void getManagersByDepartment_Returns200() throws Exception {
        when(managerService.getManagersByDepartment("D005")).thenReturn(List.of(managerResponse));

        mockMvc.perform(get("/api/v1/managers/departments/D005"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].deptNo").value("D005"))
                .andExpect(jsonPath("$.data[0].empNo").value(10001));
    }

    @Test
    @DisplayName("POST /api/v1/managers/assign - Should return 400 for missing dept no")
    void assignManager_MissingDeptNo_Returns400() throws Exception {
        ManagerAssignRequest invalid = ManagerAssignRequest.builder()
                .empNo(10001).build();

        mockMvc.perform(post("/api/v1/managers/assign")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }
}
