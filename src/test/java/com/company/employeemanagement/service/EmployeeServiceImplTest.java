package com.company.employeemanagement.service;

import com.company.employeemanagement.dto.request.EmployeeRequest;
import com.company.employeemanagement.dto.response.EmployeeResponse;
import com.company.employeemanagement.dto.response.PageResponse;
import com.company.employeemanagement.entity.Employee;
import com.company.employeemanagement.entity.Title;
import com.company.employeemanagement.enums.Gender;
import com.company.employeemanagement.exception.DuplicateResourceException;
import com.company.employeemanagement.exception.ResourceNotFoundException;
import com.company.employeemanagement.mapper.EmployeeMapper;
import com.company.employeemanagement.repository.EmployeeRepository;
import com.company.employeemanagement.repository.TitleRepository;
import com.company.employeemanagement.service.impl.EmployeeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceImplTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private TitleRepository titleRepository;

    @Mock
    private EmployeeMapper employeeMapper;

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    private Employee employee;
    private Title title;
    private EmployeeRequest employeeRequest;
    private EmployeeResponse employeeResponse;

    @BeforeEach
    void setUp() {
        title = Title.builder()
                .titleId("T001")
                .title("Software Engineer")
                .build();

        employee = Employee.builder()
                .empNo(10001)
                .title(title)
                .firstName("John")
                .lastName("Doe")
                .sex(Gender.MALE)
                .birthDate(LocalDate.of(1990, 1, 15))
                .hireDate(LocalDate.of(2020, 6, 1))
                .build();

        employeeRequest = EmployeeRequest.builder()
                .empNo(10001)
                .titleId("T001")
                .firstName("John")
                .lastName("Doe")
                .sex(Gender.MALE)
                .birthDate(LocalDate.of(1990, 1, 15))
                .hireDate(LocalDate.of(2020, 6, 1))
                .build();

        employeeResponse = EmployeeResponse.builder()
                .empNo(10001)
                .titleId("T001")
                .titleName("Software Engineer")
                .firstName("John")
                .lastName("Doe")
                .sex(Gender.MALE)
                .birthDate(LocalDate.of(1990, 1, 15))
                .hireDate(LocalDate.of(2020, 6, 1))
                .build();
    }

    @Test
    @DisplayName("Should create employee successfully")
    void createEmployee_Success() {
        when(employeeRepository.existsById(10001)).thenReturn(false);
        when(titleRepository.findById("T001")).thenReturn(Optional.of(title));
        when(employeeMapper.toEntity(employeeRequest)).thenReturn(employee);
        when(employeeRepository.save(employee)).thenReturn(employee);
        when(employeeMapper.toResponse(employee)).thenReturn(employeeResponse);

        EmployeeResponse result = employeeService.createEmployee(employeeRequest);

        assertThat(result).isNotNull();
        assertThat(result.getEmpNo()).isEqualTo(10001);
        assertThat(result.getFirstName()).isEqualTo("John");
        verify(employeeRepository).save(employee);
    }

    @Test
    @DisplayName("Should throw DuplicateResourceException when employee already exists")
    void createEmployee_Duplicate_ThrowsException() {
        when(employeeRepository.existsById(10001)).thenReturn(true);

        assertThatThrownBy(() -> employeeService.createEmployee(employeeRequest))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("10001");

        verify(employeeRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when title not found")
    void createEmployee_TitleNotFound_ThrowsException() {
        when(employeeRepository.existsById(10001)).thenReturn(false);
        when(titleRepository.findById("T001")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> employeeService.createEmployee(employeeRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("T001");
    }

    @Test
    @DisplayName("Should get employee by ID successfully")
    void getEmployeeById_Success() {
        when(employeeRepository.findById(10001)).thenReturn(Optional.of(employee));
        when(employeeMapper.toResponse(employee)).thenReturn(employeeResponse);

        EmployeeResponse result = employeeService.getEmployeeById(10001);

        assertThat(result).isNotNull();
        assertThat(result.getEmpNo()).isEqualTo(10001);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when employee not found")
    void getEmployeeById_NotFound_ThrowsException() {
        when(employeeRepository.findById(99999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> employeeService.getEmployeeById(99999))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99999");
    }

    @Test
    @DisplayName("Should return paginated employees")
    void getAllEmployees_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Employee> employeePage = new PageImpl<>(List.of(employee), pageable, 1);

        when(employeeRepository.findAll(pageable)).thenReturn(employeePage);
        when(employeeMapper.toResponse(employee)).thenReturn(employeeResponse);

        PageResponse<EmployeeResponse> result = employeeService.getAllEmployees(pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should search employees with specification")
    @SuppressWarnings("unchecked")
    void searchEmployees_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Employee> employeePage = new PageImpl<>(List.of(employee), pageable, 1);

        when(employeeRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(employeePage);
        when(employeeMapper.toResponse(employee)).thenReturn(employeeResponse);

        PageResponse<EmployeeResponse> result = employeeService.searchEmployees(
                "John", null, null, null, pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    @DisplayName("Should delete employee successfully")
    void deleteEmployee_Success() {
        when(employeeRepository.findById(10001)).thenReturn(Optional.of(employee));

        employeeService.deleteEmployee(10001);

        verify(employeeRepository).delete(employee);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException on delete when not found")
    void deleteEmployee_NotFound_ThrowsException() {
        when(employeeRepository.findById(99999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> employeeService.deleteEmployee(99999))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(employeeRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Should update employee successfully")
    void updateEmployee_Success() {
        when(employeeRepository.findById(10001)).thenReturn(Optional.of(employee));
        when(employeeRepository.save(employee)).thenReturn(employee);
        when(employeeMapper.toResponse(employee)).thenReturn(employeeResponse);

        EmployeeResponse result = employeeService.updateEmployee(10001, employeeRequest);

        assertThat(result).isNotNull();
        verify(employeeMapper).updateEntity(employeeRequest, employee);
        verify(employeeRepository).save(employee);
    }
}
