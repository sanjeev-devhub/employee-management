package com.company.employeemanagement.repository;

import com.company.employeemanagement.entity.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Integer>,
        JpaSpecificationExecutor<Employee> {

    @Query("SELECT e FROM Employee e JOIN e.departments d WHERE d.deptNo = :deptNo")
    Page<Employee> findByDepartmentDeptNo(@Param("deptNo") String deptNo, Pageable pageable);

    @Query("SELECT e FROM Employee e JOIN FETCH e.title WHERE e.empNo = :empNo")
    java.util.Optional<Employee> findByIdWithTitle(@Param("empNo") Integer empNo);

    @Query("SELECT e FROM Employee e JOIN e.managedDepartments d WHERE d.deptNo = :deptNo")
    List<Employee> findManagersByDeptNo(@Param("deptNo") String deptNo);
}
