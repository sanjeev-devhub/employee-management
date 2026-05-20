package com.company.employeemanagement.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ManagerAssignRequest {

    @NotBlank(message = "Department number is required")
    private String deptNo;

    @NotNull(message = "Employee number is required")
    @Positive(message = "Employee number must be positive")
    private Integer empNo;
}
