package com.company.employeemanagement.controller;

import com.company.employeemanagement.dto.request.ManagerAssignRequest;
import com.company.employeemanagement.dto.response.ApiResponse;
import com.company.employeemanagement.dto.response.ManagerResponse;
import com.company.employeemanagement.service.ManagerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/managers")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Managers", description = "Department manager management APIs")
public class ManagerController {

    private final ManagerService managerService;

    @PostMapping("/assign")
    @Operation(summary = "Assign a manager to a department")
    public ResponseEntity<ApiResponse<ManagerResponse>> assignManager(
            @Valid @RequestBody ManagerAssignRequest request) {
        log.info("POST /api/v1/managers/assign - dept: {}, emp: {}", request.getDeptNo(), request.getEmpNo());
        ManagerResponse response = managerService.assignManager(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created("Manager assigned successfully", response));
    }

    @DeleteMapping("/remove")
    @Operation(summary = "Remove a manager from a department")
    public ResponseEntity<ApiResponse<Void>> removeManager(
            @Valid @RequestBody ManagerAssignRequest request) {
        log.info("DELETE /api/v1/managers/remove - dept: {}, emp: {}", request.getDeptNo(), request.getEmpNo());
        managerService.removeManager(request);
        return ResponseEntity.ok(ApiResponse.success("Manager removed successfully", null));
    }

    @GetMapping
    @Operation(summary = "Get all department managers")
    public ResponseEntity<ApiResponse<List<ManagerResponse>>> getAllManagers() {
        log.info("GET /api/v1/managers");
        List<ManagerResponse> response = managerService.getAllManagers();
        return ResponseEntity.ok(ApiResponse.success("Managers fetched successfully", response));
    }

    @GetMapping("/departments/{deptNo}")
    @Operation(summary = "Get managers by department")
    public ResponseEntity<ApiResponse<List<ManagerResponse>>> getManagersByDepartment(
            @PathVariable String deptNo) {
        log.info("GET /api/v1/managers/departments/{}", deptNo);
        List<ManagerResponse> response = managerService.getManagersByDepartment(deptNo);
        return ResponseEntity.ok(ApiResponse.success("Managers fetched successfully", response));
    }
}
