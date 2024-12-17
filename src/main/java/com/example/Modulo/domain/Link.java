package com.example.Modulo.domain;

import com.example.Modulo.exception.InvalidUrlException;
import com.example.Modulo.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class Link extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "basic_info_id")
    private BasicInfo basicInfo;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String url;

    @Builder
    public Link(String title, String url) {
        this.title = title;
        validateUrl(url);
        this.url = url;
    }

    void setBasicInfo(BasicInfo basicInfo) {
        this.basicInfo = basicInfo;
    }

    private void validateUrl(String url) {
        if (url == null || !url.matches("^https?://.*")) {
            throw new InvalidUrlException();
        }
    }
}