package com.company.employeemanagement.mapper;

import com.company.employeemanagement.dto.request.TitleRequest;
import com.company.employeemanagement.dto.response.TitleResponse;
import com.company.employeemanagement.entity.Title;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface TitleMapper {

    @Mapping(source = "titleId", target = "titleId")
    @Mapping(source = "title", target = "title")
    Title toEntity(TitleRequest request);

    TitleResponse toResponse(Title title);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(TitleRequest request, @MappingTarget Title title);
}
