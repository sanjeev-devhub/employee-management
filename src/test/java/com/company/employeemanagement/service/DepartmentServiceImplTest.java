package com.company.employeemanagement.service;

import com.company.employeemanagement.dto.request.DepartmentRequest;
import com.company.employeemanagement.dto.response.DepartmentResponse;
import com.company.employeemanagement.dto.response.EmployeeResponse;
import com.company.employeemanagement.dto.response.PageResponse;
import com.company.employeemanagement.entity.Department;
import com.company.employeemanagement.entity.Employee;
import com.company.employeemanagement.enums.Gender;
import com.company.employeemanagement.exception.DuplicateResourceException;
import com.company.employeemanagement.exception.ResourceNotFoundException;
import com.company.employeemanagement.mapper.DepartmentMapper;
import com.company.employeemanagement.mapper.EmployeeMapper;
import com.company.employeemanagement.repository.DepartmentRepository;
import com.company.employeemanagement.repository.EmployeeRepository;
import com.company.employeemanagement.service.impl.DepartmentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DepartmentServiceImplTest {

    @Mock
    private DepartmentRepository departmentRepository;
    @Mock
    private EmployeeRepository employeeRepository;
    @Mock
    private DepartmentMapper departmentMapper;
    @Mock
    private EmployeeMapper employeeMapper;

    @InjectMocks
    private DepartmentServiceImpl departmentService;

    private Department department;
    private DepartmentRequest departmentRequest;
    private DepartmentResponse departmentResponse;

    @BeforeEach
    void setUp() {
        department = Department.builder()
                .deptNo("D005")
                .deptName("Development")
                .build();

        departmentRequest = DepartmentRequest.builder()
                .deptNo("D005")
                .deptName("Development")
                .build();

        departmentResponse = DepartmentResponse.builder()
                .deptNo("D005")
                .deptName("Development")
                .build();
    }

    @Test
    @DisplayName("Should create department successfully")
    void createDepartment_Success() {
        when(departmentRepository.existsById("D005")).thenReturn(false);
        when(departmentRepository.existsByDeptName("Development")).thenReturn(false);
        when(departmentMapper.toEntity(departmentRequest)).thenReturn(department);
        when(departmentRepository.save(department)).thenReturn(department);
        when(departmentMapper.toResponse(department)).thenReturn(departmentResponse);

        DepartmentResponse result = departmentService.createDepartment(departmentRequest);

        assertThat(result).isNotNull();
        assertThat(result.getDeptNo()).isEqualTo("D005");
        verify(departmentRepository).save(department);
    }

    @Test
    @DisplayName("Should throw DuplicateResourceException when dept no exists")
    void createDepartment_DuplicateId_ThrowsException() {
        when(departmentRepository.existsById("D005")).thenReturn(true);

        assertThatThrownBy(() -> departmentService.createDepartment(departmentRequest))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("D005");

        verify(departmentRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw DuplicateResourceException when dept name exists")
    void createDepartment_DuplicateName_ThrowsException() {
        when(departmentRepository.existsById("D005")).thenReturn(false);
        when(departmentRepository.existsByDeptName("Development")).thenReturn(true);

        assertThatThrownBy(() -> departmentService.createDepartment(departmentRequest))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("Development");
    }

    @Test
    @DisplayName("Should get department by ID")
    void getDepartmentById_Success() {
        when(departmentRepository.findById("D005")).thenReturn(Optional.of(department));
        when(departmentMapper.toResponse(department)).thenReturn(departmentResponse);

        DepartmentResponse result = departmentService.getDepartmentById("D005");

        assertThat(result.getDeptNo()).isEqualTo("D005");
        assertThat(result.getDeptName()).isEqualTo("Development");
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when department not found")
    void getDepartmentById_NotFound_ThrowsException() {
        when(departmentRepository.findById("D999")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> departmentService.getDepartmentById("D999"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("D999");
    }

    @Test
    @DisplayName("Should return paginated departments")
    void getAllDepartments_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Department> page = new PageImpl<>(List.of(department), pageable, 1);

        when(departmentRepository.findAll(pageable)).thenReturn(page);
        when(departmentMapper.toResponse(department)).thenReturn(departmentResponse);

        PageResponse<DepartmentResponse> result = departmentService.getAllDepartments(pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should delete department successfully")
    void deleteDepartment_Success() {
        when(departmentRepository.findById("D005")).thenReturn(Optional.of(department));

        departmentService.deleteDepartment("D005");

        verify(departmentRepository).delete(department);
    }

    @Test
    @DisplayName("Should get employees by department")
    void getEmployeesByDepartment_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        Employee employee = Employee.builder()
                .empNo(10001).firstName("John").lastName("Doe")
                .sex("MALE")
                .birthDate(LocalDate.of(1990, 1, 1))
                .hireDate(LocalDate.of(2020, 1, 1))
                .build();
        EmployeeResponse employeeResponse = EmployeeResponse.builder()
                .empNo(10001).firstName("John").lastName("Doe").build();

        when(departmentRepository.findById("D005")).thenReturn(Optional.of(department));
        when(employeeRepository.findByDepartmentDeptNo("D005", pageable))
                .thenReturn(new PageImpl<>(List.of(employee), pageable, 1));
        when(employeeMapper.toResponse(employee)).thenReturn(employeeResponse);

        PageResponse<EmployeeResponse> result = departmentService.getEmployeesByDepartment("D005", pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getEmpNo()).isEqualTo(10001);
    }
}
