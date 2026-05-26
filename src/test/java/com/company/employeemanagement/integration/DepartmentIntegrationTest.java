package com.company.employeemanagement.integration;

import com.company.employeemanagement.dto.request.DepartmentRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class DepartmentIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Integration: Should create and retrieve department")
    void createAndRetrieveDepartment() throws Exception {
        DepartmentRequest request = DepartmentRequest.builder()
                .deptNo("D010")
                .deptName("Innovation Lab")
                .build();

        mockMvc.perform(post("/api/v1/departments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.deptNo").value("D010"))
                .andExpect(jsonPath("$.data.deptName").value("Innovation Lab"));

        mockMvc.perform(get("/api/v1/departments/D010"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.deptName").value("Innovation Lab"));
    }

    @Test
    @DisplayName("Integration: Should reject duplicate department")
    void createDepartment_Duplicate_Returns409() throws Exception {
        DepartmentRequest request = DepartmentRequest.builder()
                .deptNo("D011")
                .deptName("Ops")
                .build();

        mockMvc.perform(post("/api/v1/departments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        mockMvc.perform(post("/api/v1/departments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    @DisplayName("Integration: Should update department successfully")
    void updateDepartment_Success() throws Exception {
        DepartmentRequest create = DepartmentRequest.builder()
                .deptNo("D012")
                .deptName("Old Name")
                .build();

        mockMvc.perform(post("/api/v1/departments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(create)));

        DepartmentRequest update = DepartmentRequest.builder()
                .deptNo("D012")
                .deptName("New Name")
                .build();

        mockMvc.perform(put("/api/v1/departments/D012")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.deptName").value("New Name"));
    }

    @Test
    @DisplayName("Integration: Should delete department successfully")
    void deleteDepartment_Success() throws Exception {
        DepartmentRequest request = DepartmentRequest.builder()
                .deptNo("D013")
                .deptName("Temp Dept")
                .build();

        mockMvc.perform(post("/api/v1/departments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        mockMvc.perform(delete("/api/v1/departments/D013"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/departments/D013"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Integration: Should get all departments paginated")
    void getAllDepartments_Paginated() throws Exception {
        mockMvc.perform(get("/api/v1/departments")
                        .param("page", "0")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.pageSize").value(5));
    }
}
