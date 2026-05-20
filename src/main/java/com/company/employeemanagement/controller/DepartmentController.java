package com.company.employeemanagement.controller;

import com.company.employeemanagement.dto.request.DepartmentRequest;
import com.company.employeemanagement.dto.response.ApiResponse;
import com.company.employeemanagement.dto.response.DepartmentResponse;
import com.company.employeemanagement.dto.response.EmployeeResponse;
import com.company.employeemanagement.dto.response.PageResponse;
import com.company.employeemanagement.service.DepartmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/departments")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Departments", description = "Department management APIs")
public class DepartmentController {

    private final DepartmentService departmentService;

    @PostMapping
    @Operation(summary = "Create a new department")
    public ResponseEntity<ApiResponse<DepartmentResponse>> createDepartment(
            @Valid @RequestBody DepartmentRequest request) {
        log.info("POST /api/v1/departments");
        DepartmentResponse response = departmentService.createDepartment(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created("Department created successfully", response));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a department")
    public ResponseEntity<ApiResponse<DepartmentResponse>> updateDepartment(
            @PathVariable String id,
            @Valid @RequestBody DepartmentRequest request) {
        log.info("PUT /api/v1/departments/{}", id);
        DepartmentResponse response = departmentService.updateDepartment(id, request);
        return ResponseEntity.ok(ApiResponse.success("Department updated successfully", response));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a department")
    public ResponseEntity<ApiResponse<Void>> deleteDepartment(@PathVariable String id) {
        log.info("DELETE /api/v1/departments/{}", id);
        departmentService.deleteDepartment(id);
        return ResponseEntity.ok(ApiResponse.success("Department deleted successfully", null));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get department by ID")
    public ResponseEntity<ApiResponse<DepartmentResponse>> getDepartmentById(@PathVariable String id) {
        log.info("GET /api/v1/departments/{}", id);
        DepartmentResponse response = departmentService.getDepartmentById(id);
        return ResponseEntity.ok(ApiResponse.success("Department fetched successfully", response));
    }

    @GetMapping
    @Operation(summary = "Get all departments with pagination")
    public ResponseEntity<ApiResponse<PageResponse<DepartmentResponse>>> getAllDepartments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "deptNo,asc") String[] sort) {
        log.info("GET /api/v1/departments - page: {}, size: {}", page, size);
        Pageable pageable = PageRequest.of(page, size, buildSort(sort));
        PageResponse<DepartmentResponse> response = departmentService.getAllDepartments(pageable);
        return ResponseEntity.ok(ApiResponse.success("Departments fetched successfully", response));
    }

    @GetMapping("/{deptNo}/employees")
    @Operation(summary = "Get all employees in a department")
    public ResponseEntity<ApiResponse<PageResponse<EmployeeResponse>>> getEmployeesByDepartment(
            @PathVariable String deptNo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("GET /api/v1/departments/{}/employees", deptNo);
        Pageable pageable = PageRequest.of(page, size, Sort.by("firstName").ascending());
        PageResponse<EmployeeResponse> response = departmentService.getEmployeesByDepartment(deptNo, pageable);
        return ResponseEntity.ok(ApiResponse.success("Employees fetched successfully", response));
    }

    private Sort buildSort(String[] sort) {
        if (sort.length == 2) {
            return Sort.by(Sort.Direction.fromString(sort[1]), sort[0]);
        }
        return Sort.by("deptNo").ascending();
    }
}
