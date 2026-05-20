package com.company.employeemanagement.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "salaries")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Salary {

    @Id
    @Column(name = "emp_no")
    private Integer empNo;

    @Column(name = "salary", nullable = false)
    private Integer salary;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "emp_no")
    private Employee employee;
}
