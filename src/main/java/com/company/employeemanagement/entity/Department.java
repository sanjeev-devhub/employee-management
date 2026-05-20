package com.company.employeemanagement.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "departments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Department {

    @Id
    @Column(name = "dept_no", length = 10)
    private String deptNo;

    @Column(name = "dept_name", length = 100, unique = true, nullable = false)
    private String deptName;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "dept_emp",
            joinColumns = @JoinColumn(name = "dept_no"),
            inverseJoinColumns = @JoinColumn(name = "emp_no")
    )
    @Builder.Default
    private Set<Employee> employees = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "dept_manager",
            joinColumns = @JoinColumn(name = "dept_no"),
            inverseJoinColumns = @JoinColumn(name = "emp_no")
    )
    @Builder.Default
    private Set<Employee> managers = new HashSet<>();
}
