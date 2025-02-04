package com.example.Modulo.domain;

import com.example.Modulo.exception.InvalidResumeTitleException;
import com.example.Modulo.global.common.BaseTimeEntity;
import com.example.Modulo.global.enums.ResumeTheme;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Table(indexes = @Index(name = "idx_resume_member", columnList = "member_id"))
public class Resume extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(nullable = false)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ResumeTheme theme;

    @OneToMany(mappedBy = "resume", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ResumeSection> sections = new ArrayList<>();

    @Builder
    public Resume(Member member, String title, ResumeTheme theme) {
        validateTitle(title);
        this.member = member;
        this.title = title;
        this.theme = theme != null ? theme : ResumeTheme.BASIC;
    }

    public void updateTitle(String title) {
        validateTitle(title);
        this.title = title;
    }

    public void updateTheme(ResumeTheme theme) {
        this.theme = theme != null ? theme : this.theme;
    }

    public void updateSections(List<ResumeSection> sections) {
        this.sections.clear();
        sections.forEach(section -> section.setResume(this));
        this.sections.addAll(sections);
    }

    private void validateTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new InvalidResumeTitleException();
        }
    }
}