package com.example.Modulo.domain;

import com.example.Modulo.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class Resume extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(nullable = false)
    private String title;

    @OneToMany(mappedBy = "resume", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ResumeSection> sections = new ArrayList<>();

    @Builder
    public Resume(Member member, String title) {
        this.member = member;
        this.title = title;
    }

    public void updateTitle(String title) {
        this.title = title;
    }

    public void updateSections(List<ResumeSection> sections) {
        this.sections.clear();
        sections.forEach(section -> section.setResume(this));
        this.sections.addAll(sections);
    }
}