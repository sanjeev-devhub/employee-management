package com.company.employeemanagement.service.impl;

import com.company.employeemanagement.config.RedisCacheConfig;
import com.company.employeemanagement.dto.request.SalaryRequest;
import com.company.employeemanagement.dto.response.SalaryResponse;
import com.company.employeemanagement.entity.Employee;
import com.company.employeemanagement.entity.Salary;
import com.company.employeemanagement.exception.ResourceNotFoundException;
import com.company.employeemanagement.repository.EmployeeRepository;
import com.company.employeemanagement.repository.SalaryRepository;
import com.company.employeemanagement.service.SalaryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class SalaryServiceImpl implements SalaryService {

    private final SalaryRepository salaryRepository;
    private final EmployeeRepository employeeRepository;

    @Override
    @Transactional
    @CachePut(value = RedisCacheConfig.SALARY_CACHE, key = "#empNo")
    public SalaryResponse updateSalary(Integer empNo, SalaryRequest request) {
        log.info("Updating salary for employee: {}", empNo);
        Employee employee = findEmployeeOrThrow(empNo);

        Salary salary = salaryRepository.findByEmployee_EmpNo(empNo)
                .orElse(Salary.builder().employee(employee).build());

        salary.setSalary(request.getSalary());
        Salary saved = salaryRepository.save(salary);
        log.info("Salary updated for employee: {}", empNo);
        return buildSalaryResponse(employee, saved);
    }

    @Override
    @Cacheable(value = RedisCacheConfig.SALARY_CACHE, key = "#empNo")
    public SalaryResponse getSalaryByEmployee(Integer empNo) {
        log.info("Fetching salary from DB for employee: {}", empNo);
        Employee employee = findEmployeeOrThrow(empNo);
        Salary salary = salaryRepository.findByEmployee_EmpNo(empNo)
                .orElseThrow(() -> new ResourceNotFoundException("Salary", "empNo", empNo));
        return buildSalaryResponse(employee, salary);
    }

    private Employee findEmployeeOrThrow(Integer empNo) {
        return employeeRepository.findById(empNo)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "empNo", empNo));
    }

    private SalaryResponse buildSalaryResponse(Employee employee, Salary salary) {
        return SalaryResponse.builder()
                .empNo(employee.getEmpNo())
                .firstName(employee.getFirstName())
                .lastName(employee.getLastName())
                .salary(salary.getSalary())
                .build();
    }
}
