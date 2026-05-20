package com.company.employeemanagement.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeptEmpId implements Serializable {

    @Column(name = "emp_no")
    private Integer empNo;

    @Column(name = "dept_no", length = 10)
    private String deptNo;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DeptEmpId that)) return false;
        return Objects.equals(empNo, that.empNo) && Objects.equals(deptNo, that.deptNo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(empNo, deptNo);
    }
}
