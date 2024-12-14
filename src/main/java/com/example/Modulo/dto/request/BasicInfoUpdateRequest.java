package com.example.Modulo.dto.request;

import com.example.Modulo.global.enums.CareerYear;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class BasicInfoUpdateRequest {
    private String profileImageUrl;
    private String name;
    private String email;
    private CareerYear careerYear;
    private Integer birthYear;
    private String jobPosition;
    private String shortBio;
    private List<LinkRequest> links;
    private List<String> techStack;

    @Getter
    @NoArgsConstructor
    public static class LinkRequest {
        private String title;
        private String url;
    }
}