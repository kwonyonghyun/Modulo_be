package com.example.Modulo.domain;

import com.example.Modulo.global.common.BaseTimeEntity;
import com.example.Modulo.global.converter.YearMonthConverter;
import com.example.Modulo.global.enums.EducationLevel;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.YearMonth;

@Entity
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class Education extends BaseTimeEntity {

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
    @Column(nullable = false)
    private YearMonth endDate;

    @Column(nullable = false)
    private String school;

    @Column(nullable = false)
    private String major;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EducationLevel educationLevel;

    @Builder
    public Education(Member member, YearMonth startDate, YearMonth endDate, String school,
                     String major, EducationLevel educationLevel) {
        this.member = member;
        this.startDate = startDate;
        this.endDate = endDate;
        this.school = school;
        this.major = major;
        this.educationLevel = educationLevel;
    }

    public void update(YearMonth startDate, YearMonth endDate, String school,
                       String major, EducationLevel educationLevel){
        this.startDate = startDate;
        this.endDate = endDate;
        this.school = school;
        this.major = major;
        this.educationLevel = educationLevel;
    }
}
