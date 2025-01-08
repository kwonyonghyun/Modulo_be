package com.example.Modulo.domain;

import com.example.Modulo.global.common.BaseTimeEntity;
import com.example.Modulo.global.enums.CareerYear;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class BasicInfo extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private String profileImageUrl;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CareerYear careerYear;

    @Column(nullable = false)
    private Integer birthYear;

    @Column(nullable = false)
    private String jobPosition;

    @Column(columnDefinition = "TEXT")
    private String shortBio;

    @OneToMany(mappedBy = "basicInfo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Link> links = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "basic_info_tech_stack", joinColumns = @JoinColumn(name = "basic_info_id"))
    private List<String> techStack = new ArrayList<>();

    @Builder
    public BasicInfo(Member member, String profileImageUrl, String name, String email,
                     CareerYear careerYear, Integer birthYear, String jobPosition,
                     String shortBio) {
        this.member = member;
        this.profileImageUrl = profileImageUrl;
        this.name = name;
        this.email = email;
        this.careerYear = careerYear;
        this.birthYear = birthYear;
        this.jobPosition = jobPosition;
        this.shortBio = shortBio;
    }

    public void update(String profileImageUrl, String name, String email,
                       CareerYear careerYear, Integer birthYear, String jobPosition,
                       String shortBio) {
        this.profileImageUrl = profileImageUrl;
        this.name = name;
        this.email = email;
        this.careerYear = careerYear;
        this.birthYear = birthYear;
        this.jobPosition = jobPosition;
        this.shortBio = shortBio;
    }

    public void updateLinks(List<Link> newLinks) {
        this.links.clear();
        if (newLinks != null) {
            newLinks.forEach(link -> link.setBasicInfo(this));
            this.links.addAll(newLinks);
        }
    }

    public void updateTechStack(List<String> techStack) {
        this.techStack.clear();
        if (techStack != null) {
            this.techStack.addAll(techStack);
        }
    }
}