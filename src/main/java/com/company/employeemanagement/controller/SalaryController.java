package com.company.employeemanagement.controller;

import com.company.employeemanagement.dto.request.SalaryRequest;
import com.company.employeemanagement.dto.response.ApiResponse;
import com.company.employeemanagement.dto.response.SalaryResponse;
import com.company.employeemanagement.service.SalaryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/salaries")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Salaries", description = "Salary management APIs")
public class SalaryController {

    private final SalaryService salaryService;

    @PutMapping("/{empNo}")
    @Operation(summary = "Update or set employee salary")
    public ResponseEntity<ApiResponse<SalaryResponse>> updateSalary(
            @PathVariable Integer empNo,
            @Valid @RequestBody SalaryRequest request) {
        log.info("PUT /api/v1/salaries/{}", empNo);
        SalaryResponse response = salaryService.updateSalary(empNo, request);
        return ResponseEntity.ok(ApiResponse.success("Salary updated successfully", response));
    }

    @GetMapping("/{empNo}")
    @Operation(summary = "Get salary by employee number")
    public ResponseEntity<ApiResponse<SalaryResponse>> getSalary(@PathVariable Integer empNo) {
        log.info("GET /api/v1/salaries/{}", empNo);
        SalaryResponse response = salaryService.getSalaryByEmployee(empNo);
        return ResponseEntity.ok(ApiResponse.success("Salary fetched successfully", response));
    }
}
