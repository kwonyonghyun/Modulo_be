package com.example.Modulo.dto.request;

import com.example.Modulo.global.enums.EtcType;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.YearMonth;

@Getter
@NoArgsConstructor
public class EtcUpdateRequest {
    private YearMonth startDate;
    private YearMonth endDate;
    private String title;
    private String description;
    private EtcType type;
    private String organization;
    private String score;
} 