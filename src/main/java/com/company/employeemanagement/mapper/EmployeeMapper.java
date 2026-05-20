package com.company.employeemanagement.mapper;

import com.company.employeemanagement.dto.request.EmployeeRequest;
import com.company.employeemanagement.dto.response.EmployeeResponse;
import com.company.employeemanagement.entity.Department;
import com.company.employeemanagement.entity.Employee;
import org.mapstruct.*;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {

    @Mapping(target = "title", ignore = true)
    @Mapping(target = "salary", ignore = true)
    @Mapping(target = "departments", ignore = true)
    @Mapping(target = "managedDepartments", ignore = true)
    @Mapping(target = "sex", ignore = true)   // handled in service: MALE/FEMALE → "M"/"F"
    Employee toEntity(EmployeeRequest request);

    @Mapping(source = "title.titleId", target = "titleId")
    @Mapping(source = "title.title",   target = "titleName")
    @Mapping(source = "salary.salary", target = "salary")
    @Mapping(target = "sex",           ignore = true)   // set by service after mapping
    @Mapping(target = "departments",   expression = "java(mapDepartmentNames(employee))")
    EmployeeResponse toResponse(Employee employee);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "title",             ignore = true)
    @Mapping(target = "salary",            ignore = true)
    @Mapping(target = "departments",       ignore = true)
    @Mapping(target = "managedDepartments",ignore = true)
    @Mapping(target = "sex",               ignore = true)   // handled in service
    void updateEntity(EmployeeRequest request, @MappingTarget Employee employee);

    default Set<String> mapDepartmentNames(Employee employee) {
        if (employee.getDepartments() == null) return Set.of();
        return employee.getDepartments().stream()
                .map(Department::getDeptName)
                .collect(Collectors.toSet());
    }
}
