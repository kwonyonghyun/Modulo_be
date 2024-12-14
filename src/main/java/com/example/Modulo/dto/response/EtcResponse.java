package com.example.Modulo.dto.response;

import com.example.Modulo.domain.Etc;
import com.example.Modulo.global.enums.EtcType;
import lombok.Builder;
import lombok.Getter;

import java.time.YearMonth;

@Getter
@Builder
public class EtcResponse {
    private Long id;
    private YearMonth startDate;
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