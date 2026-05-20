package com.company.employeemanagement.repository;

import com.company.employeemanagement.entity.Salary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SalaryRepository extends JpaRepository<Salary, Integer> {

    Optional<Salary> findByEmployee_EmpNo(Integer empNo);
}
