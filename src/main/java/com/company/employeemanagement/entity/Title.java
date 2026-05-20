package com.company.employeemanagement.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "titles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Title {

    @Id
    @Column(name = "title_id", length = 10)
    private String titleId;

    @Column(name = "title", length = 100, unique = true, nullable = false)
    private String title;

    @OneToMany(mappedBy = "title", fetch = FetchType.LAZY)
    @Builder.Default
    private List<Employee> employees = new ArrayList<>();
}
