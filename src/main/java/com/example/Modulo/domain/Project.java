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
public class Project extends BaseTimeEntity {

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
    private String projectName;

    @Column(columnDefinition = "TEXT")
    private String shortDescription;

    @ElementCollection
    @CollectionTable(name = "project_tech_stack", joinColumns = @JoinColumn(name = "project_id"))
    private List<String> techStack = new ArrayList<>();

    @Column(nullable = false)
    private String teamComposition;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String detailedDescription;

    @Builder
    public Project(Member member, YearMonth startDate, YearMonth endDate, String projectName,
                   String shortDescription, List<String> techStack, String teamComposition,
                   String detailedDescription) {
        this.member = member;
        this.startDate = startDate;
        this.endDate = endDate;
        this.projectName = projectName;
        this.shortDescription = shortDescription;
        this.techStack = techStack;
        this.teamComposition = teamComposition;
        this.detailedDescription = detailedDescription;
    }

    public void update(YearMonth startDate, YearMonth endDate, String projectName,
                       String shortDescription, List<String> techStack, String teamComposition,
                       String detailedDescription) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.projectName = projectName;
        this.shortDescription = shortDescription;
        this.techStack = techStack;
        this.teamComposition = teamComposition;
        this.detailedDescription = detailedDescription;
    }
}