package com.example.Modulo.domain;

import com.example.Modulo.global.common.BaseTimeEntity;
import com.example.Modulo.global.converter.YearMonthConverter;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class Career extends BaseTimeEntity {

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
    private String companyName;

    @Column(columnDefinition = "TEXT")
    private String companyDescription;

    @Column(nullable = false)
    private String position;

    @ElementCollection
    @CollectionTable(name = "career_tech_stack", joinColumns = @JoinColumn(name = "career_id"))
    private List<String> techStack = new ArrayList<>();

    @Column(columnDefinition = "TEXT", nullable = false)
    private String achievements;

    @Builder
    public Career(Member member, YearMonth startDate, YearMonth endDate, String companyName,
                  String companyDescription, String position, List<String> techStack, String achievements) {
        this.member = member;
        this.startDate = startDate;
        this.endDate = endDate;
        this.companyName = companyName;
        this.companyDescription = companyDescription;
        this.position = position;
        this.techStack = techStack;
        this.achievements = achievements;
    }

    public void update(YearMonth startDate, YearMonth endDate, String companyName,
                       String companyDescription, String position, List<String> techStack, String achievements) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.companyName = companyName;
        this.companyDescription = companyDescription;
        this.position = position;
        this.techStack = techStack;
        this.achievements = achievements;
    }
}