package com.company.employeemanagement.enums;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class GenderConverter implements AttributeConverter<Gender, String> {

    @Override
    public String convertToDatabaseColumn(Gender gender) {
        if (gender == null) return null;
        return gender.getCode();
    }

    @Override
    public Gender convertToEntityAttribute(String dbValue) {
        if (dbValue == null) return null;
        return Gender.fromCode(dbValue);
    }
}
