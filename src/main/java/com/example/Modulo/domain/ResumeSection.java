package com.example.Modulo.domain;

import com.example.Modulo.exception.InvalidResumeSectionException;
import com.example.Modulo.global.common.BaseTimeEntity;
import com.example.Modulo.global.enums.SectionType;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class ResumeSection extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resume_id")
    private Resume resume;

    @Column(nullable = false)
    private Integer orderIndex;

    @Column(nullable = false)
    private Integer topMargin;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SectionType sectionType;

    @OneToMany(mappedBy = "resumeSection", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SectionContent> contents = new ArrayList<>();

    @Builder
    public ResumeSection(Resume resume, Integer orderIndex, Integer topMargin, SectionType sectionType) {
        validateFields(orderIndex, topMargin, sectionType);
        this.resume = resume;
        this.orderIndex = orderIndex;
        this.topMargin = topMargin;
        this.sectionType = sectionType;
    }

    public void setResume(Resume resume) {
        this.resume = resume;
    }

    public void update(Integer orderIndex, Integer topMargin) {
        validateFields(orderIndex, topMargin, this.sectionType);
        this.orderIndex = orderIndex;
        this.topMargin = topMargin;
    }

    public void updateContents(List<SectionContent> contents) {
        this.contents.clear();
        if (contents != null) {
            contents.forEach(content -> content.setResumeSection(this));
            this.contents.addAll(contents);
        }
    }

    private void validateFields(Integer orderIndex, Integer topMargin, SectionType sectionType) {
        if (orderIndex == null || orderIndex < 0 ||
                topMargin == null || topMargin < 0 ||
                sectionType == null) {
            throw new InvalidResumeSectionException();
        }
    }
}