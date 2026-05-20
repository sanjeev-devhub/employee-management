package com.company.employeemanagement.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "employees")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Employee {

    @Id
    @Column(name = "emp_no")
    private Integer empNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emp_title_id", nullable = false)
    private Title title;

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @Column(name = "first_name", length = 50, nullable = false)
    private String firstName;

    @Column(name = "last_name", length = 50, nullable = false)
    private String lastName;

    /**
     * Stored as single char in DB: 'M' or 'F'.
     * Converted to/from Gender enum explicitly in the service layer.
     */
    @Column(name = "sex", length = 1)
    private String sex;

    @Column(name = "hire_date", nullable = false)
    private LocalDate hireDate;

    @OneToOne(mappedBy = "employee", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Salary salary;

    @ManyToMany(mappedBy = "employees", fetch = FetchType.LAZY)
    @Builder.Default
    private Set<Department> departments = new HashSet<>();

    @ManyToMany(mappedBy = "managers", fetch = FetchType.LAZY)
    @Builder.Default
    private Set<Department> managedDepartments = new HashSet<>();
}
