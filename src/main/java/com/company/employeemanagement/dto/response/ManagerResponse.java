package com.company.employeemanagement.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ManagerResponse {

    private String deptNo;
    private String deptName;
    private Integer empNo;
    private String firstName;
    private String lastName;
}
