package com.example.Modulo.domain;

import com.example.Modulo.exception.InvalidSectionContentException;
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
        validateFields(orderIndex, topMargin, contentId);
        this.resumeSection = resumeSection;
        this.orderIndex = orderIndex;
        this.topMargin = topMargin;
        this.contentId = contentId;
    }

    public void setResumeSection(ResumeSection resumeSection) {
        this.resumeSection = resumeSection;
    }

    public void update(Integer orderIndex, Integer topMargin) {
        validateFields(orderIndex, topMargin, this.contentId);
        this.orderIndex = orderIndex;
        this.topMargin = topMargin;
    }

    private void validateFields(Integer orderIndex, Integer topMargin, Long contentId) {
        if (orderIndex == null || orderIndex < 0 ||
                topMargin == null || topMargin < 0 ||
                contentId == null) {
            throw new InvalidSectionContentException();
        }
    }
}