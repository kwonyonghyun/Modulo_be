package com.example.Modulo.domain;

import com.example.Modulo.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class SectionContent extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resume_section_id")
    private ResumeSection resumeSection;

    @Column(nullable = false)
    private Integer orderIndex;

    @Column(nullable = false)
    private Integer topMargin;

    @Column(nullable = false)
    private Long contentId;

    @Builder
    public SectionContent(ResumeSection resumeSection, Integer orderIndex, Integer topMargin, Long contentId) {
        this.resumeSection = resumeSection;
        this.orderIndex = orderIndex;
        this.topMargin = topMargin;
        this.contentId = contentId;
    }

    public void setResumeSection(ResumeSection resumeSection) {
        this.resumeSection = resumeSection;
    }

    public void update(Integer orderIndex, Integer topMargin) {
        this.orderIndex = orderIndex;
        this.topMargin = topMargin;
    }
}