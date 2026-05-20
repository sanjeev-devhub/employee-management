package com.company.employeemanagement.specification;

import com.company.employeemanagement.entity.Department;
import com.company.employeemanagement.entity.Employee;
import com.company.employeemanagement.entity.Title;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class EmployeeSpecification {

    private EmployeeSpecification() {}

    public static Specification<Employee> filterBy(
            String firstName,
            String lastName,
            String department,
            String title) {

        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(firstName)) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("firstName")),
                        "%" + firstName.toLowerCase() + "%"
                ));
            }

            if (StringUtils.hasText(lastName)) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("lastName")),
                        "%" + lastName.toLowerCase() + "%"
                ));
            }

            if (StringUtils.hasText(department)) {
                Join<Employee, Department> deptJoin = root.join("departments", JoinType.INNER);
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(deptJoin.get("deptName")),
                        "%" + department.toLowerCase() + "%"
                ));
                if (query != null) query.distinct(true);
            }

            if (StringUtils.hasText(title)) {
                Join<Employee, Title> titleJoin = root.join("title", JoinType.INNER);
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(titleJoin.get("title")),
                        "%" + title.toLowerCase() + "%"
                ));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
