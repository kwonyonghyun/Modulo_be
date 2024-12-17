package com.example.Modulo.domain;

import com.example.Modulo.exception.InvalidSelfIntroductionFieldException;
import com.example.Modulo.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class SelfIntroduction extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Builder
    public SelfIntroduction(Member member, String title, String content) {
        validateFields(title, content);
        this.member = member;
        this.title = title;
        this.content = content;
    }

    public void update(String title, String content) {
        validateFields(title, content);
        this.title = title;
        this.content = content;
    }

    private void validateFields(String title, String content) {
        if (title == null || title.trim().isEmpty() ||
                content == null || content.trim().isEmpty()) {
            throw new InvalidSelfIntroductionFieldException();
        }
    }
}