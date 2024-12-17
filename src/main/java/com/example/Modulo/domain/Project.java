package com.example.Modulo.domain;

import com.example.Modulo.exception.InvalidProjectDateException;
import com.example.Modulo.exception.InvalidProjectFieldException;
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

    @Column(columnDefinition = "TEXT", nullable = false)
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
        validateDate(startDate, endDate);
        validateFields(projectName, shortDescription, techStack, teamComposition, detailedDescription);
        this.member = member;
        this.startDate = startDate;
        this.endDate = endDate;
        this.projectName = projectName;
        this.shortDescription = shortDescription;
        this.techStack = new ArrayList<>(techStack);
        this.teamComposition = teamComposition;
        this.detailedDescription = detailedDescription;
    }

    public void update(YearMonth startDate, YearMonth endDate, String projectName,
                       String shortDescription, List<String> techStack, String teamComposition,
                       String detailedDescription) {
        validateDate(startDate, endDate);
        validateFields(projectName, shortDescription, techStack, teamComposition, detailedDescription);
        this.startDate = startDate;
        this.endDate = endDate;
        this.projectName = projectName;
        this.shortDescription = shortDescription;
        this.techStack = new ArrayList<>(techStack);
        this.teamComposition = teamComposition;
        this.detailedDescription = detailedDescription;
    }

    private void validateDate(YearMonth startDate, YearMonth endDate) {
        if (startDate == null || endDate == null || startDate.isAfter(endDate)) {
            throw new InvalidProjectDateException();
        }
    }

    private void validateFields(String projectName, String shortDescription,
                                List<String> techStack, String teamComposition,
                                String detailedDescription) {
        if (projectName == null || projectName.trim().isEmpty() ||
                shortDescription == null || shortDescription.trim().isEmpty() ||
                techStack == null || techStack.isEmpty() ||
                teamComposition == null || teamComposition.trim().isEmpty() ||
                detailedDescription == null || detailedDescription.trim().isEmpty()) {
            throw new InvalidProjectFieldException();
        }
    }
}