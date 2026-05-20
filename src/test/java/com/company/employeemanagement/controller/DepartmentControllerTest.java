package com.company.employeemanagement.controller;

import com.company.employeemanagement.dto.request.DepartmentRequest;
import com.company.employeemanagement.dto.response.ApiResponse;
import com.company.employeemanagement.dto.response.DepartmentResponse;
import com.company.employeemanagement.dto.response.PageResponse;
import com.company.employeemanagement.exception.DuplicateResourceException;
import com.company.employeemanagement.exception.GlobalExceptionHandler;
import com.company.employeemanagement.exception.ResourceNotFoundException;
import com.company.employeemanagement.service.DepartmentService;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class DepartmentControllerTest {

    @Mock
    private DepartmentService departmentService;

    @InjectMocks
    private DepartmentController departmentController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private DepartmentResponse departmentResponse;
    private DepartmentRequest departmentRequest;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(departmentController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        objectMapper = new ObjectMapper();

        departmentResponse = DepartmentResponse.builder()
                .deptNo("D001")
                .deptName("Engineering")
                .build();

        departmentRequest = DepartmentRequest.builder()
                .deptNo("D001")
                .deptName("Engineering")
                .build();
    }

    @Test
    @DisplayName("POST /api/v1/departments - Should create department and return 201")
    void createDepartment_Returns201() throws Exception {
        when(departmentService.createDepartment(any(DepartmentRequest.class))).thenReturn(departmentResponse);

        mockMvc.perform(post("/api/v1/departments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(departmentRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.data.deptNo").value("D001"))
                .andExpect(jsonPath("$.data.deptName").value("Engineering"));
    }

    @Test
    @DisplayName("POST /api/v1/departments - Should return 409 on duplicate")
    void createDepartment_Duplicate_Returns409() throws Exception {
        when(departmentService.createDepartment(any(DepartmentRequest.class)))
                .thenThrow(new DuplicateResourceException("Department", "deptNo", "D001"));

        mockMvc.perform(post("/api/v1/departments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(departmentRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    @DisplayName("GET /api/v1/departments/{id} - Should return department by ID")
    void getDepartmentById_Returns200() throws Exception {
        when(departmentService.getDepartmentById("D001")).thenReturn(departmentResponse);

        mockMvc.perform(get("/api/v1/departments/D001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.deptNo").value("D001"))
                .andExpect(jsonPath("$.data.deptName").value("Engineering"));
    }

    @Test
    @DisplayName("GET /api/v1/departments/{id} - Should return 404 when not found")
    void getDepartmentById_NotFound_Returns404() throws Exception {
        when(departmentService.getDepartmentById("D999"))
                .thenThrow(new ResourceNotFoundException("Department", "deptNo", "D999"));

        mockMvc.perform(get("/api/v1/departments/D999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/v1/departments - Should return paginated departments")
    void getAllDepartments_Returns200() throws Exception {
        PageResponse<DepartmentResponse> pageResponse = PageResponse.<DepartmentResponse>builder()
                .content(List.of(departmentResponse))
                .pageNumber(0)
                .pageSize(10)
                .totalElements(1)
                .totalPages(1)
                .first(true)
                .last(true)
                .build();

        when(departmentService.getAllDepartments(any())).thenReturn(pageResponse);

        mockMvc.perform(get("/api/v1/departments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.totalElements").value(1));
    }

    @Test
    @DisplayName("PUT /api/v1/departments/{id} - Should update department")
    void updateDepartment_Returns200() throws Exception {
        DepartmentResponse updated = DepartmentResponse.builder()
                .deptNo("D001")
                .deptName("Engineering Updated")
                .build();

        when(departmentService.updateDepartment(eq("D001"), any(DepartmentRequest.class))).thenReturn(updated);

        mockMvc.perform(put("/api/v1/departments/D001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(departmentRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.deptNo").value("D001"));
    }

    @Test
    @DisplayName("DELETE /api/v1/departments/{id} - Should delete department")
    void deleteDepartment_Returns200() throws Exception {
        doNothing().when(departmentService).deleteDepartment("D001");

        mockMvc.perform(delete("/api/v1/departments/D001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Department deleted successfully"));
    }
}
