package com.example.Modulo.domain;

import com.example.Modulo.global.common.BaseTimeEntity;
import com.example.Modulo.global.converter.YearMonthConverter;
import com.example.Modulo.global.enums.EtcType;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.YearMonth;

@Entity
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class Etc extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Convert(converter = YearMonthConverter.class)
    @Column(nullable = false)
    private YearMonth startDate;

    @Convert(converter = YearMonthConverter.class)
    private YearMonth endDate;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EtcType type;

    @Column
    private String organization;

    @Column
    private String score;

    @Builder
    public Etc(Member member, YearMonth startDate, YearMonth endDate, String title,
               String description, EtcType type, String organization, String score) {
        this.member = member;
        this.startDate = startDate;
        this.endDate = endDate;
        this.title = title;
        this.description = description;
        this.type = type;
        this.organization = organization;
        this.score = score;
    }

    public void update(YearMonth startDate, YearMonth endDate, String title,
                      String description, EtcType type, String organization, String score) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.title = title;
        this.description = description;
        this.type = type;
        this.organization = organization;
        this.score = score;
    }
} 