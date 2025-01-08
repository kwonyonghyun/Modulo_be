package com.example.Modulo.dto.response;

import com.example.Modulo.domain.Etc;
import com.example.Modulo.global.enums.EtcType;
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
public class EtcResponse implements Serializable {
    private Long id;

    @JsonSerialize(using = YearMonthSerializer.class)
    @JsonDeserialize(using = YearMonthDeserializer.class)
    private YearMonth startDate;

    @JsonSerialize(using = YearMonthSerializer.class)
    @JsonDeserialize(using = YearMonthDeserializer.class)
    private YearMonth endDate;


    private String title;
    private String description;
    private EtcType type;
    private String organization;
    private String score;

    public static EtcResponse from(Etc etc) {
        return EtcResponse.builder()
                .id(etc.getId())
                .startDate(etc.getStartDate())
                .endDate(etc.getEndDate())
                .title(etc.getTitle())
                .description(etc.getDescription())
                .type(etc.getType())
                .organization(etc.getOrganization())
                .score(etc.getScore())
                .build();
    }
} 