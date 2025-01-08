package com.example.Modulo.dto.response;

import com.example.Modulo.domain.Career;
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
import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CareerResponse implements Serializable {
    private Long id;

    @JsonSerialize(using = YearMonthSerializer.class)
    @JsonDeserialize(using = YearMonthDeserializer.class)
    private YearMonth startDate;

    @JsonSerialize(using = YearMonthSerializer.class)
    @JsonDeserialize(using = YearMonthDeserializer.class)
    private YearMonth endDate;
    private String companyName;
    private String companyDescription;
    private String position;
    private List<String> techStack;
    private String achievements;

    public static CareerResponse from(Career career) {
        return CareerResponse.builder()
                .id(career.getId())
                .startDate(career.getStartDate())
                .endDate(career.getEndDate())
                .companyName(career.getCompanyName())
                .companyDescription(career.getCompanyDescription())
                .position(career.getPosition())
                .techStack(new ArrayList<>(career.getTechStack()))
                .achievements(career.getAchievements())
                .build();
    }
}