package com.company.employeemanagement.repository;

import com.company.employeemanagement.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, String> {

    boolean existsByDeptName(String deptName);

    Optional<Department> findByDeptName(String deptName);
}
