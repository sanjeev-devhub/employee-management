package com.company.employeemanagement.dto.response;

import com.company.employeemanagement.enums.Gender;
import lombok.*;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeResponse {

    private Integer empNo;
    private String titleId;
    private String titleName;
    private LocalDate birthDate;
    private String firstName;
    private String lastName;
    private Gender sex;
    private LocalDate hireDate;
    private Integer salary;
    private Set<String> departments;
}
