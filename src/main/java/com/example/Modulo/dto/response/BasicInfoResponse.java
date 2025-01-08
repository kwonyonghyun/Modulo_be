package com.example.Modulo.dto.response;

import com.example.Modulo.domain.BasicInfo;
import com.example.Modulo.domain.Link;
import com.example.Modulo.global.enums.CareerYear;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BasicInfoResponse implements Serializable {
    private Long id;
    private String profileImageUrl;
    private String name;
    private String email;
    private CareerYear careerYear;
    private Integer birthYear;
    private String jobPosition;
    private String shortBio;
    private List<LinkResponse> links;
    private List<String> techStack;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LinkResponse {
        private Long id;
        private String title;
        private String url;

        public static LinkResponse from(Link link) {
            return LinkResponse.builder()
                    .id(link.getId())
                    .title(link.getTitle())
                    .url(link.getUrl())
                    .build();
        }
    }

    public static BasicInfoResponse from(BasicInfo basicInfo) {
        return BasicInfoResponse.builder()
                .id(basicInfo.getId())
                .profileImageUrl(basicInfo.getProfileImageUrl())
                .name(basicInfo.getName())
                .email(basicInfo.getEmail())
                .careerYear(basicInfo.getCareerYear())
                .birthYear(basicInfo.getBirthYear())
                .jobPosition(basicInfo.getJobPosition())
                .shortBio(basicInfo.getShortBio())
                .links(basicInfo.getLinks().stream()
                        .map(LinkResponse::from)
                        .collect(Collectors.toList()))
                .techStack(new ArrayList<>(basicInfo.getTechStack()))
                .build();
    }
}