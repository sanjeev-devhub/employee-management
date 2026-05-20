package com.company.employeemanagement.service;

import com.company.employeemanagement.dto.request.DepartmentRequest;
import com.company.employeemanagement.dto.response.DepartmentResponse;
import com.company.employeemanagement.dto.response.EmployeeResponse;
import com.company.employeemanagement.dto.response.PageResponse;
import org.springframework.data.domain.Pageable;

public interface DepartmentService {

    DepartmentResponse createDepartment(DepartmentRequest request);

    DepartmentResponse updateDepartment(String deptNo, DepartmentRequest request);

    void deleteDepartment(String deptNo);

    DepartmentResponse getDepartmentById(String deptNo);

    PageResponse<DepartmentResponse> getAllDepartments(Pageable pageable);

    PageResponse<EmployeeResponse> getEmployeesByDepartment(String deptNo, Pageable pageable);
}
