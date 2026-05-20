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
public class DeptManagerId implements Serializable {

    @Column(name = "dept_no", length = 10)
    private String deptNo;

    @Column(name = "emp_no")
    private Integer empNo;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DeptManagerId that)) return false;
        return Objects.equals(deptNo, that.deptNo) && Objects.equals(empNo, that.empNo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(deptNo, empNo);
    }
}
