package com.company.employeemanagement.service;

import com.company.employeemanagement.dto.request.EmployeeRequest;
import com.company.employeemanagement.dto.response.EmployeeResponse;
import com.company.employeemanagement.dto.response.PageResponse;
import org.springframework.data.domain.Pageable;

public interface EmployeeService {

    EmployeeResponse createEmployee(EmployeeRequest request);

    EmployeeResponse updateEmployee(Integer empNo, EmployeeRequest request);

    void deleteEmployee(Integer empNo);

    EmployeeResponse getEmployeeById(Integer empNo);

    PageResponse<EmployeeResponse> getAllEmployees(Pageable pageable);

    PageResponse<EmployeeResponse> searchEmployees(
            String firstName,
            String lastName,
            String department,
            String title,
            Pageable pageable);
}
