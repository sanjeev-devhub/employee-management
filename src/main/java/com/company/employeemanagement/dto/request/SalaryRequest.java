package com.company.employeemanagement.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalaryRequest {

    @NotNull(message = "Salary is required")
    @Positive(message = "Salary must be positive")
    private Integer salary;
}
