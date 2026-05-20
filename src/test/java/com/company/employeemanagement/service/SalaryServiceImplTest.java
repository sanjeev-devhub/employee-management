package com.company.employeemanagement.service;

import com.company.employeemanagement.dto.request.SalaryRequest;
import com.company.employeemanagement.dto.response.SalaryResponse;
import com.company.employeemanagement.entity.Employee;
import com.company.employeemanagement.entity.Salary;
import com.company.employeemanagement.enums.Gender;
import com.company.employeemanagement.exception.ResourceNotFoundException;
import com.company.employeemanagement.repository.EmployeeRepository;
import com.company.employeemanagement.repository.SalaryRepository;
import com.company.employeemanagement.service.impl.SalaryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SalaryServiceImplTest {

    @Mock
    private SalaryRepository salaryRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private SalaryServiceImpl salaryService;

    private Employee employee;
    private Salary salary;
    private SalaryRequest salaryRequest;

    @BeforeEach
    void setUp() {
        employee = Employee.builder()
                .empNo(10001)
                .firstName("John")
                .lastName("Doe")
                .sex("M")
                .birthDate(LocalDate.of(1990, 1, 15))
                .hireDate(LocalDate.of(2020, 6, 1))
                .build();

        salary = Salary.builder()
                .empNo(10001)
                .salary(80000)
                .employee(employee)
                .build();

        salaryRequest = SalaryRequest.builder().salary(90000).build();
    }

    @Test
    @DisplayName("Should update existing salary successfully")
    void updateSalary_ExistingSalary_Success() {
        when(employeeRepository.findById(10001)).thenReturn(Optional.of(employee));
        when(salaryRepository.findByEmployee_EmpNo(10001)).thenReturn(Optional.of(salary));
        when(salaryRepository.save(any(Salary.class))).thenReturn(salary);

        SalaryResponse result = salaryService.updateSalary(10001, salaryRequest);

        assertThat(result).isNotNull();
        assertThat(result.getEmpNo()).isEqualTo(10001);
        verify(salaryRepository).save(any(Salary.class));
    }

    @Test
    @DisplayName("Should create new salary when not exists")
    void updateSalary_NewSalary_Success() {
        when(employeeRepository.findById(10001)).thenReturn(Optional.of(employee));
        when(salaryRepository.findByEmployee_EmpNo(10001)).thenReturn(Optional.empty());
        when(salaryRepository.save(any(Salary.class))).thenReturn(salary);

        SalaryResponse result = salaryService.updateSalary(10001, salaryRequest);

        assertThat(result).isNotNull();
        verify(salaryRepository).save(any(Salary.class));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when employee not found on update")
    void updateSalary_EmployeeNotFound_ThrowsException() {
        when(employeeRepository.findById(99999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> salaryService.updateSalary(99999, salaryRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99999");
    }

    @Test
    @DisplayName("Should get salary by employee number")
    void getSalaryByEmployee_Success() {
        when(employeeRepository.findById(10001)).thenReturn(Optional.of(employee));
        when(salaryRepository.findByEmployee_EmpNo(10001)).thenReturn(Optional.of(salary));

        SalaryResponse result = salaryService.getSalaryByEmployee(10001);

        assertThat(result).isNotNull();
        assertThat(result.getSalary()).isEqualTo(80000);
        assertThat(result.getFirstName()).isEqualTo("John");
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when salary not found")
    void getSalaryByEmployee_NotFound_ThrowsException() {
        when(employeeRepository.findById(10001)).thenReturn(Optional.of(employee));
        when(salaryRepository.findByEmployee_EmpNo(10001)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> salaryService.getSalaryByEmployee(10001))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("10001");
    }
}
