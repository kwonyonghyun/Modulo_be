package com.example.Modulo.global.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.time.YearMonth;

@Converter(autoApply = true)
public class YearMonthConverter implements AttributeConverter<YearMonth, String> {

    @Override
    public String convertToDatabaseColumn(YearMonth yearMonth) {
        if (yearMonth != null) {
            return yearMonth.toString();
        }
        return null;
    }

    @Override
    public YearMonth convertToEntityAttribute(String dbData) {
        if (dbData != null) {
            return YearMonth.parse(dbData);
        }
        return null;
    }
}