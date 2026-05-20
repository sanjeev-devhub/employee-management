package com.company.employeemanagement.controller;

import com.company.employeemanagement.dto.request.EmployeeRequest;
import com.company.employeemanagement.dto.response.ApiResponse;
import com.company.employeemanagement.dto.response.EmployeeResponse;
import com.company.employeemanagement.dto.response.PageResponse;
import com.company.employeemanagement.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/employees")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Employees", description = "Employee management APIs")
public class EmployeeController {

    private final EmployeeService employeeService;

    @PostMapping
    @Operation(summary = "Create a new employee")
    public ResponseEntity<ApiResponse<EmployeeResponse>> createEmployee(
            @Valid @RequestBody EmployeeRequest request) {
        log.info("POST /api/v1/employees - empNo: {}", request.getEmpNo());
        EmployeeResponse response = employeeService.createEmployee(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created("Employee created successfully", response));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an employee")
    public ResponseEntity<ApiResponse<EmployeeResponse>> updateEmployee(
            @PathVariable Integer id,
            @Valid @RequestBody EmployeeRequest request) {
        log.info("PUT /api/v1/employees/{}", id);
        EmployeeResponse response = employeeService.updateEmployee(id, request);
        return ResponseEntity.ok(ApiResponse.success("Employee updated successfully", response));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an employee")
    public ResponseEntity<ApiResponse<Void>> deleteEmployee(@PathVariable Integer id) {
        log.info("DELETE /api/v1/employees/{}", id);
        employeeService.deleteEmployee(id);
        return ResponseEntity.ok(ApiResponse.success("Employee deleted successfully", null));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get employee by ID")
    public ResponseEntity<ApiResponse<EmployeeResponse>> getEmployeeById(@PathVariable Integer id) {
        log.info("GET /api/v1/employees/{}", id);
        EmployeeResponse response = employeeService.getEmployeeById(id);
        return ResponseEntity.ok(ApiResponse.success("Employee fetched successfully", response));
    }

    @GetMapping
    @Operation(summary = "Get all employees with pagination and sorting")
    @Cacheable("employees")
    public ResponseEntity<ApiResponse<PageResponse<EmployeeResponse>>> getAllEmployees(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "firstName,asc") String[] sort) {
        log.info("GET /api/v1/employees - page: {}, size: {}", page, size);
        Pageable pageable = PageRequest.of(page, size, buildSort(sort));
        PageResponse<EmployeeResponse> response = employeeService.getAllEmployees(pageable);
        return ResponseEntity.ok(ApiResponse.success("Employees fetched successfully", response));
    }

    @GetMapping("/search")
    @Operation(summary = "Search employees by first name, last name, department, or title")
    public ResponseEntity<ApiResponse<PageResponse<EmployeeResponse>>> searchEmployees(
            @Parameter(description = "First name filter") @RequestParam(required = false) String firstName,
            @Parameter(description = "Last name filter") @RequestParam(required = false) String lastName,
            @Parameter(description = "Department name filter") @RequestParam(required = false) String department,
            @Parameter(description = "Title name filter") @RequestParam(required = false) String title,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "firstName,asc") String[] sort) {
        log.info("GET /api/v1/employees/search");
        Pageable pageable = PageRequest.of(page, size, buildSort(sort));
        PageResponse<EmployeeResponse> response = employeeService.searchEmployees(
                firstName, lastName, department, title, pageable);
        return ResponseEntity.ok(ApiResponse.success("Employees search completed", response));
    }

    private Sort buildSort(String[] sort) {
        if (sort.length == 2) {
            return Sort.by(Sort.Direction.fromString(sort[1]), sort[0]);
        }
        return Sort.by("firstName").ascending();
    }
}
