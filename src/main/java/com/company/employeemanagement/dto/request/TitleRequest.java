package com.company.employeemanagement.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TitleRequest {

    @NotBlank(message = "Title ID is required")
    @Size(max = 10, message = "Title ID must not exceed 10 characters")
    private String titleId;

    @NotBlank(message = "Title name is required")
    @Size(max = 100, message = "Title must not exceed 100 characters")
    private String title;
}
