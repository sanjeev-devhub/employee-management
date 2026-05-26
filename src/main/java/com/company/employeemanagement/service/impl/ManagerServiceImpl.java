package com.company.employeemanagement.service.impl;

import com.company.employeemanagement.config.RedisCacheConfig;
import com.company.employeemanagement.dto.request.ManagerAssignRequest;
import com.company.employeemanagement.dto.response.ManagerResponse;
import com.company.employeemanagement.entity.Department;
import com.company.employeemanagement.entity.Employee;
import com.company.employeemanagement.exception.ResourceNotFoundException;
import com.company.employeemanagement.repository.DepartmentRepository;
import com.company.employeemanagement.repository.EmployeeRepository;
import com.company.employeemanagement.service.ManagerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ManagerServiceImpl implements ManagerService {

    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = RedisCacheConfig.MANAGER_CACHE, key = "'ALL'"),
            @CacheEvict(value = RedisCacheConfig.MANAGER_CACHE, key = "#request.deptNo")
    })
    public ManagerResponse assignManager(ManagerAssignRequest request) {
        log.info("Assigning manager {} to department {}", request.getEmpNo(), request.getDeptNo());
        Department department = findDepartmentOrThrow(request.getDeptNo());
        Employee employee = findEmployeeOrThrow(request.getEmpNo());
        department.getManagers().add(employee);
        departmentRepository.save(department);
        log.info("Manager assigned successfully");
        return buildManagerResponse(department, employee);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = RedisCacheConfig.MANAGER_CACHE, key = "'ALL'"),
            @CacheEvict(value = RedisCacheConfig.MANAGER_CACHE, key = "#request.deptNo")
    })
    public void removeManager(ManagerAssignRequest request) {
        log.info("Removing manager {} from department {}", request.getEmpNo(), request.getDeptNo());
        Department department = findDepartmentOrThrow(request.getDeptNo());
        Employee employee = findEmployeeOrThrow(request.getEmpNo());
        boolean removed = department.getManagers().removeIf(m -> m.getEmpNo().equals(employee.getEmpNo()));
        if (!removed) {
            throw new ResourceNotFoundException(
                    "Manager assignment not found for department: " + request.getDeptNo() +
                    " and employee: " + request.getEmpNo());
        }
        departmentRepository.save(department);
        log.info("Manager removed successfully");
    }

    @Override
    @Cacheable(value = RedisCacheConfig.MANAGER_CACHE, key = "'ALL'")
    public List<ManagerResponse> getAllManagers() {
        log.info("Fetching all managers from DB");
        List<ManagerResponse> responses = new ArrayList<>();
        List<Department> departments = departmentRepository.findAll();
        for (Department dept : departments) {
            for (Employee manager : dept.getManagers()) {
                responses.add(buildManagerResponse(dept, manager));
            }
        }
        return responses;
    }

    @Override
    @Cacheable(value = RedisCacheConfig.MANAGER_CACHE, key = "#deptNo")
    public List<ManagerResponse> getManagersByDepartment(String deptNo) {
        log.info("Fetching managers from DB for department: {}", deptNo);
        Department department = findDepartmentOrThrow(deptNo);
        List<ManagerResponse> responses = new ArrayList<>();
        for (Employee manager : department.getManagers()) {
            responses.add(buildManagerResponse(department, manager));
        }
        return responses;
    }

    private Department findDepartmentOrThrow(String deptNo) {
        return departmentRepository.findById(deptNo)
                .orElseThrow(() -> new ResourceNotFoundException("Department", "deptNo", deptNo));
    }

    private Employee findEmployeeOrThrow(Integer empNo) {
        return employeeRepository.findById(empNo)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "empNo", empNo));
    }

    private ManagerResponse buildManagerResponse(Department dept, Employee employee) {
        return ManagerResponse.builder()
                .deptNo(dept.getDeptNo())
                .deptName(dept.getDeptName())
                .empNo(employee.getEmpNo())
                .firstName(employee.getFirstName())
                .lastName(employee.getLastName())
                .build();
    }
}
