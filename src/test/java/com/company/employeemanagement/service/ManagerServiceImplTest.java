package com.company.employeemanagement.service;

import com.company.employeemanagement.dto.request.ManagerAssignRequest;
import com.company.employeemanagement.dto.response.ManagerResponse;
import com.company.employeemanagement.entity.Department;
import com.company.employeemanagement.entity.Employee;
import com.company.employeemanagement.enums.Gender;
import com.company.employeemanagement.exception.ResourceNotFoundException;
import com.company.employeemanagement.repository.DepartmentRepository;
import com.company.employeemanagement.repository.EmployeeRepository;
import com.company.employeemanagement.service.impl.ManagerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ManagerServiceImplTest {

    @Mock
    private DepartmentRepository departmentRepository;
    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private ManagerServiceImpl managerService;

    private Department department;
    private Employee employee;
    private ManagerAssignRequest assignRequest;

    @BeforeEach
    void setUp() {
        employee = Employee.builder()
                .empNo(10001)
                .firstName("John")
                .lastName("Doe")
                .sex("MALE")
                .birthDate(LocalDate.of(1985, 5, 10))
                .hireDate(LocalDate.of(2010, 3, 1))
                .build();

        department = Department.builder()
                .deptNo("D005")
                .deptName("Development")
                .managers(new HashSet<>())
                .employees(new HashSet<>())
                .build();

        assignRequest = ManagerAssignRequest.builder()
                .deptNo("D005")
                .empNo(10001)
                .build();
    }

    @Test
    @DisplayName("Should assign manager successfully")
    void assignManager_Success() {
        when(departmentRepository.findById("D005")).thenReturn(Optional.of(department));
        when(employeeRepository.findById(10001)).thenReturn(Optional.of(employee));
        when(departmentRepository.save(department)).thenReturn(department);

        ManagerResponse result = managerService.assignManager(assignRequest);

        assertThat(result).isNotNull();
        assertThat(result.getDeptNo()).isEqualTo("D005");
        assertThat(result.getEmpNo()).isEqualTo(10001);
        assertThat(result.getFirstName()).isEqualTo("John");
        verify(departmentRepository).save(department);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when department not found on assign")
    void assignManager_DepartmentNotFound_ThrowsException() {
        when(departmentRepository.findById("D999")).thenReturn(Optional.empty());
        assignRequest = ManagerAssignRequest.builder().deptNo("D999").empNo(10001).build();

        assertThatThrownBy(() -> managerService.assignManager(assignRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("D999");
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when employee not found on assign")
    void assignManager_EmployeeNotFound_ThrowsException() {
        when(departmentRepository.findById("D005")).thenReturn(Optional.of(department));
        when(employeeRepository.findById(99999)).thenReturn(Optional.empty());
        assignRequest = ManagerAssignRequest.builder().deptNo("D005").empNo(99999).build();

        assertThatThrownBy(() -> managerService.assignManager(assignRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99999");
    }

    @Test
    @DisplayName("Should remove manager successfully")
    void removeManager_Success() {
        department.getManagers().add(employee);
        when(departmentRepository.findById("D005")).thenReturn(Optional.of(department));
        when(employeeRepository.findById(10001)).thenReturn(Optional.of(employee));
        when(departmentRepository.save(department)).thenReturn(department);

        managerService.removeManager(assignRequest);

        assertThat(department.getManagers()).isEmpty();
        verify(departmentRepository).save(department);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when removing non-existent manager")
    void removeManager_NotAssigned_ThrowsException() {
        when(departmentRepository.findById("D005")).thenReturn(Optional.of(department));
        when(employeeRepository.findById(10001)).thenReturn(Optional.of(employee));

        assertThatThrownBy(() -> managerService.removeManager(assignRequest))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(departmentRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should get all managers across departments")
    void getAllManagers_Success() {
        department.getManagers().add(employee);
        when(departmentRepository.findAll()).thenReturn(List.of(department));

        List<ManagerResponse> result = managerService.getAllManagers();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDeptName()).isEqualTo("Development");
        assertThat(result.get(0).getFirstName()).isEqualTo("John");
    }

    @Test
    @DisplayName("Should get managers by department")
    void getManagersByDepartment_Success() {
        department.getManagers().add(employee);
        when(departmentRepository.findById("D005")).thenReturn(Optional.of(department));

        List<ManagerResponse> result = managerService.getManagersByDepartment("D005");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getEmpNo()).isEqualTo(10001);
    }

    @Test
    @DisplayName("Should return empty list when department has no managers")
    void getManagersByDepartment_NoManagers_ReturnsEmpty() {
        when(departmentRepository.findById("D005")).thenReturn(Optional.of(department));

        List<ManagerResponse> result = managerService.getManagersByDepartment("D005");

        assertThat(result).isEmpty();
    }
}
