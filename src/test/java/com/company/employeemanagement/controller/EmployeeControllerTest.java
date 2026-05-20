package com.company.employeemanagement.controller;

import com.company.employeemanagement.dto.request.EmployeeRequest;
import com.company.employeemanagement.dto.response.EmployeeResponse;
import com.company.employeemanagement.dto.response.PageResponse;
import com.company.employeemanagement.enums.Gender;
import com.company.employeemanagement.exception.GlobalExceptionHandler;
import com.company.employeemanagement.exception.ResourceNotFoundException;
import com.company.employeemanagement.service.EmployeeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class EmployeeControllerTest {

    @Mock
    private EmployeeService employeeService;

    @InjectMocks
    private EmployeeController employeeController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private EmployeeResponse employeeResponse;
    private EmployeeRequest employeeRequest;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(employeeController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        employeeResponse = EmployeeResponse.builder()
                .empNo(10001)
                .titleId("T001")
                .titleName("Software Engineer")
                .firstName("John")
                .lastName("Doe")
                .sex(Gender.MALE)
                .birthDate(LocalDate.of(1990, 1, 15))
                .hireDate(LocalDate.of(2020, 6, 1))
                .departments(Set.of("Engineering"))
                .build();

        employeeRequest = EmployeeRequest.builder()
                .empNo(10001)
                .titleId("T001")
                .firstName("John")
                .lastName("Doe")
                .sex(Gender.MALE)
                .birthDate(LocalDate.of(1990, 1, 15))
                .hireDate(LocalDate.of(2020, 6, 1))
                .build();
    }

    @Test
    @DisplayName("POST /api/v1/employees - Should create employee and return 201")
    void createEmployee_Returns201() throws Exception {
        when(employeeService.createEmployee(any(EmployeeRequest.class))).thenReturn(employeeResponse);

        mockMvc.perform(post("/api/v1/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employeeRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.data.empNo").value(10001))
                .andExpect(jsonPath("$.data.firstName").value("John"));
    }

    @Test
    @DisplayName("GET /api/v1/employees/{id} - Should return employee by ID")
    void getEmployeeById_Returns200() throws Exception {
        when(employeeService.getEmployeeById(10001)).thenReturn(employeeResponse);

        mockMvc.perform(get("/api/v1/employees/10001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.empNo").value(10001))
                .andExpect(jsonPath("$.data.lastName").value("Doe"));
    }

    @Test
    @DisplayName("GET /api/v1/employees/{id} - Should return 404 when not found")
    void getEmployeeById_NotFound_Returns404() throws Exception {
        when(employeeService.getEmployeeById(99999))
                .thenThrow(new ResourceNotFoundException("Employee", "empNo", 99999));

        mockMvc.perform(get("/api/v1/employees/99999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @DisplayName("GET /api/v1/employees - Should return paginated employees")
    void getAllEmployees_Returns200() throws Exception {
        PageResponse<EmployeeResponse> pageResponse = PageResponse.<EmployeeResponse>builder()
                .content(List.of(employeeResponse))
                .pageNumber(0)
                .pageSize(10)
                .totalElements(1)
                .totalPages(1)
                .first(true)
                .last(true)
                .build();

        when(employeeService.getAllEmployees(any())).thenReturn(pageResponse);

        mockMvc.perform(get("/api/v1/employees")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.totalElements").value(1));
    }

    @Test
    @DisplayName("DELETE /api/v1/employees/{id} - Should delete and return 200")
    void deleteEmployee_Returns200() throws Exception {
        doNothing().when(employeeService).deleteEmployee(10001);

        mockMvc.perform(delete("/api/v1/employees/10001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Employee deleted successfully"));
    }

    @Test
    @DisplayName("POST /api/v1/employees - Should return 400 for invalid request")
    void createEmployee_InvalidRequest_Returns400() throws Exception {
        EmployeeRequest invalidRequest = EmployeeRequest.builder()
                .empNo(-1)
                .firstName("")
                .build();

        mockMvc.perform(post("/api/v1/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/v1/employees/search - Should return searched employees")
    void searchEmployees_Returns200() throws Exception {
        PageResponse<EmployeeResponse> pageResponse = PageResponse.<EmployeeResponse>builder()
                .content(List.of(employeeResponse))
                .pageNumber(0)
                .pageSize(10)
                .totalElements(1)
                .totalPages(1)
                .first(true)
                .last(true)
                .build();

        when(employeeService.searchEmployees(anyString(), isNull(), isNull(), isNull(), any()))
                .thenReturn(pageResponse);

        mockMvc.perform(get("/api/v1/employees/search")
                        .param("firstName", "John"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].firstName").value("John"));
    }
}
