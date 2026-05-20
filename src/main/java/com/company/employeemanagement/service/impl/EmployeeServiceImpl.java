package com.company.employeemanagement.service.impl;

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
import com.company.employeemanagement.service.EmployeeService;
import com.company.employeemanagement.specification.EmployeeSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final TitleRepository titleRepository;
    private final EmployeeMapper employeeMapper;

    @Override
    @Transactional
    public EmployeeResponse createEmployee(EmployeeRequest request) {
        log.info("Creating employee with ID: {}", request.getEmpNo());

        if (employeeRepository.existsById(request.getEmpNo())) {
            throw new DuplicateResourceException("Employee", "empNo", request.getEmpNo());
        }

        Title title = findTitleOrThrow(request.getTitleId());
        Employee employee = employeeMapper.toEntity(request);
        employee.setTitle(title);
        employee.setSex(toDbSex(request.getSex()));   // MALE → "M", FEMALE → "F"

        Employee saved = employeeRepository.save(employee);
        log.info("Employee created: {}", saved.getEmpNo());
        return toResponse(saved);
    }

    @Override
    @Transactional
    public EmployeeResponse updateEmployee(Integer empNo, EmployeeRequest request) {
        log.info("Updating employee: {}", empNo);

        Employee employee = findEmployeeOrThrow(empNo);

        if (!request.getTitleId().equals(employee.getTitle().getTitleId())) {
            Title title = findTitleOrThrow(request.getTitleId());
            employee.setTitle(title);
        }

        employeeMapper.updateEntity(request, employee);
        employee.setSex(toDbSex(request.getSex()));   // keep sex in sync

        Employee updated = employeeRepository.save(employee);
        log.info("Employee updated: {}", empNo);
        return toResponse(updated);
    }

    @Override
    @Transactional
    public void deleteEmployee(Integer empNo) {
        log.info("Deleting employee: {}", empNo);
        Employee employee = findEmployeeOrThrow(empNo);
        employeeRepository.delete(employee);
        log.info("Employee deleted: {}", empNo);
    }

    @Override
    public EmployeeResponse getEmployeeById(Integer empNo) {
        log.info("Fetching employee: {}", empNo);
        return toResponse(findEmployeeOrThrow(empNo));
    }

    @Override
    public PageResponse<EmployeeResponse> getAllEmployees(Pageable pageable) {
        log.info("Fetching all employees, page: {}", pageable.getPageNumber());
        Page<EmployeeResponse> page = employeeRepository.findAll(pageable)
                .map(this::toResponse);
        return PageResponse.of(page);
    }

    @Override
    public PageResponse<EmployeeResponse> searchEmployees(
            String firstName, String lastName, String department, String title, Pageable pageable) {
        log.info("Searching employees - firstName: {}, lastName: {}, dept: {}, title: {}",
                firstName, lastName, department, title);
        Specification<Employee> spec = EmployeeSpecification.filterBy(firstName, lastName, department, title);
        Page<EmployeeResponse> page = employeeRepository.findAll(spec, pageable)
                .map(this::toResponse);
        return PageResponse.of(page);
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    /**
     * Converts Gender enum from the request (MALE/FEMALE)
     * to the single-char value stored in the DB ('M'/'F').
     */
    private String toDbSex(Gender gender) {
        if (gender == null) return null;
        return gender.getCode();   // Gender.MALE.getCode() == "M"
    }

    /**
     * Converts the single-char DB value ('M'/'F') back to the
     * Gender enum for the API response.
     */
    private Gender fromDbSex(String dbValue) {
        if (dbValue == null) return null;
        return Gender.fromCode(dbValue);
    }

    /**
     * Builds EmployeeResponse, manually handling the sex conversion
     * so the mapper never needs to know about the DB format.
     */
    private EmployeeResponse toResponse(Employee employee) {
        EmployeeResponse response = employeeMapper.toResponse(employee);
        response.setSex(fromDbSex(employee.getSex()));
        return response;
    }

    private Employee findEmployeeOrThrow(Integer empNo) {
        return employeeRepository.findById(empNo)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "empNo", empNo));
    }

    private Title findTitleOrThrow(String titleId) {
        return titleRepository.findById(titleId)
                .orElseThrow(() -> new ResourceNotFoundException("Title", "titleId", titleId));
    }
}
