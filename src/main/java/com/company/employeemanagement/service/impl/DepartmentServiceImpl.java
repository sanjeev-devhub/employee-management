package com.company.employeemanagement.service.impl;

import com.company.employeemanagement.dto.request.DepartmentRequest;
import com.company.employeemanagement.dto.response.DepartmentResponse;
import com.company.employeemanagement.dto.response.EmployeeResponse;
import com.company.employeemanagement.dto.response.PageResponse;
import com.company.employeemanagement.entity.Department;
import com.company.employeemanagement.exception.DuplicateResourceException;
import com.company.employeemanagement.exception.ResourceNotFoundException;
import com.company.employeemanagement.mapper.DepartmentMapper;
import com.company.employeemanagement.mapper.EmployeeMapper;
import com.company.employeemanagement.repository.DepartmentRepository;
import com.company.employeemanagement.repository.EmployeeRepository;
import com.company.employeemanagement.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;
    private final DepartmentMapper departmentMapper;
    private final EmployeeMapper employeeMapper;

    @Override
    @Transactional
    public DepartmentResponse createDepartment(DepartmentRequest request) {
        log.info("Creating department: {}", request.getDeptNo());
        if (departmentRepository.existsById(request.getDeptNo())) {
            throw new DuplicateResourceException("Department", "deptNo", request.getDeptNo());
        }
        if (departmentRepository.existsByDeptName(request.getDeptName())) {
            throw new DuplicateResourceException("Department", "deptName", request.getDeptName());
        }
        Department department = departmentMapper.toEntity(request);
        Department saved = departmentRepository.save(department);
        log.info("Department created: {}", saved.getDeptNo());
        return departmentMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public DepartmentResponse updateDepartment(String deptNo, DepartmentRequest request) {
        log.info("Updating department: {}", deptNo);
        Department department = findDepartmentOrThrow(deptNo);
        if (!department.getDeptName().equals(request.getDeptName()) &&
                departmentRepository.existsByDeptName(request.getDeptName())) {
            throw new DuplicateResourceException("Department", "deptName", request.getDeptName());
        }
        departmentMapper.updateEntity(request, department);
        Department updated = departmentRepository.save(department);
        log.info("Department updated: {}", deptNo);
        return departmentMapper.toResponse(updated);
    }

    @Override
    @Transactional
    public void deleteDepartment(String deptNo) {
        log.info("Deleting department: {}", deptNo);
        Department department = findDepartmentOrThrow(deptNo);
        departmentRepository.delete(department);
        log.info("Department deleted: {}", deptNo);
    }

    @Override
    public DepartmentResponse getDepartmentById(String deptNo) {
        log.info("Fetching department: {}", deptNo);
        return departmentMapper.toResponse(findDepartmentOrThrow(deptNo));
    }

    @Override
    public PageResponse<DepartmentResponse> getAllDepartments(Pageable pageable) {
        log.info("Fetching all departments, page: {}", pageable.getPageNumber());
        Page<DepartmentResponse> page = departmentRepository.findAll(pageable)
                .map(departmentMapper::toResponse);
        return PageResponse.of(page);
    }

    @Override
    public PageResponse<EmployeeResponse> getEmployeesByDepartment(String deptNo, Pageable pageable) {
        log.info("Fetching employees for department: {}", deptNo);
        findDepartmentOrThrow(deptNo);
        Page<EmployeeResponse> page = employeeRepository
                .findByDepartmentDeptNo(deptNo, pageable)
                .map(employeeMapper::toResponse);
        return PageResponse.of(page);
    }

    private Department findDepartmentOrThrow(String deptNo) {
        return departmentRepository.findById(deptNo)
                .orElseThrow(() -> new ResourceNotFoundException("Department", "deptNo", deptNo));
    }
}
