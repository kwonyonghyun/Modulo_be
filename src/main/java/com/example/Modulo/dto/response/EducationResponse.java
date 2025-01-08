package com.example.Modulo.dto.response;

import com.example.Modulo.domain.Education;
import com.example.Modulo.global.enums.EducationLevel;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.YearMonthDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.YearMonthSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.YearMonth;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EducationResponse implements Serializable {
    private Long id;

    @JsonSerialize(using = YearMonthSerializer.class)
    @JsonDeserialize(using = YearMonthDeserializer.class)
    private YearMonth startDate;

    @JsonSerialize(using = YearMonthSerializer.class)
    @JsonDeserialize(using = YearMonthDeserializer.class)
    private YearMonth endDate;


    private String school;
    private String major;
    private EducationLevel educationLevel;

    public static EducationResponse from(Education education) {
        return EducationResponse.builder()
                .id(education.getId())
                .startDate(education.getStartDate())
                .endDate(education.getEndDate())
                .school(education.getSchool())
                .major(education.getMajor())
                .educationLevel(education.getEducationLevel())
                .build();
    }
}