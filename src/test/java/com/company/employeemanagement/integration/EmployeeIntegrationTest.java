//package com.company.employeemanagement.integration;
//
//import com.company.employeemanagement.dto.request.EmployeeRequest;
//import com.company.employeemanagement.dto.request.TitleRequest;
//import com.company.employeemanagement.enums.Gender;
//import com.company.employeemanagement.repository.EmployeeRepository;
//import com.company.employeemanagement.repository.TitleRepository;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.MediaType;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.LocalDate;
//
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@SpringBootTest
//@AutoConfigureMockMvc
//@ActiveProfiles("test")
//@Transactional
//class EmployeeIntegrationTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @Autowired
//    private TitleRepository titleRepository;
//
//    @Autowired
//    private EmployeeRepository employeeRepository;
//
//    @BeforeEach
//    void setUp() throws Exception {
//        // Create a title first via API
//        TitleRequest titleRequest = TitleRequest.builder()
//                .titleId("T001")
//                .title("Software Engineer")
//                .build();
//
//        mockMvc.perform(post("/api/v1/titles")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(titleRequest)));
//    }
//
//    @Test
//    @DisplayName("Integration: Should create and retrieve employee")
//    void createAndRetrieveEmployee() throws Exception {
//        EmployeeRequest request = EmployeeRequest.builder()
//                .empNo(10001)
//                .titleId("T001")
//                .firstName("Jane")
//                .lastName("Smith")
//                .sex(Gender.FEMALE)
//                .birthDate(LocalDate.of(1992, 3, 25))
//                .hireDate(LocalDate.of(2021, 9, 1))
//                .build();
//
//        // Create
//        mockMvc.perform(post("/api/v1/employees")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.data.empNo").value(10001))
//                .andExpect(jsonPath("$.data.firstName").value("Jane"));
//
//        // Retrieve
//        mockMvc.perform(get("/api/v1/employees/10001"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.data.empNo").value(10001))
//                .andExpect(jsonPath("$.data.lastName").value("Smith"));
//    }
//
//    @Test
//    @DisplayName("Integration: Should return 404 for non-existent employee")
//    void getEmployee_NotFound() throws Exception {
//        mockMvc.perform(get("/api/v1/employees/99999"))
//                .andExpect(status().isNotFound())
//                .andExpect(jsonPath("$.status").value(404));
//    }
//
//    @Test
//    @DisplayName("Integration: Should return 400 for invalid employee creation")
//    void createEmployee_InvalidRequest() throws Exception {
//        EmployeeRequest invalidRequest = EmployeeRequest.builder()
//                .empNo(-1)
//                .build();
//
//        mockMvc.perform(post("/api/v1/employees")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(invalidRequest)))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    @DisplayName("Integration: Should create and delete employee")
//    void createAndDeleteEmployee() throws Exception {
//        EmployeeRequest request = EmployeeRequest.builder()
//                .empNo(10002)
//                .titleId("T001")
//                .firstName("Bob")
//                .lastName("Johnson")
//                .sex(Gender.MALE)
//                .birthDate(LocalDate.of(1985, 7, 10))
//                .hireDate(LocalDate.of(2019, 3, 15))
//                .build();
//
//        mockMvc.perform(post("/api/v1/employees")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isCreated());
//
//        mockMvc.perform(delete("/api/v1/employees/10002"))
//                .andExpect(status().isOk());
//
//        mockMvc.perform(get("/api/v1/employees/10002"))
//                .andExpect(status().isNotFound());
//    }
//
//    @Test
//    @DisplayName("Integration: Should search employees by first name")
//    void searchEmployees_ByFirstName() throws Exception {
//        EmployeeRequest request = EmployeeRequest.builder()
//                .empNo(10003)
//                .titleId("T001")
//                .firstName("Alice")
//                .lastName("Wonder")
//                .sex(Gender.FEMALE)
//                .birthDate(LocalDate.of(1993, 11, 5))
//                .hireDate(LocalDate.of(2022, 1, 10))
//                .build();
//
//        mockMvc.perform(post("/api/v1/employees")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(request)));
//
//        mockMvc.perform(get("/api/v1/employees/search")
//                        .param("firstName", "Alice"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.data.content[0].firstName").value("Alice"));
//    }
//}
