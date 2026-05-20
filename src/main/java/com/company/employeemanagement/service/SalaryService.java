package com.company.employeemanagement.service;

import com.company.employeemanagement.dto.request.SalaryRequest;
import com.company.employeemanagement.dto.response.SalaryResponse;

public interface SalaryService {

    SalaryResponse updateSalary(Integer empNo, SalaryRequest request);

    SalaryResponse getSalaryByEmployee(Integer empNo);
}
