package com.example.Modulo.dto.response;

import com.example.Modulo.domain.SelfIntroduction;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SelfIntroductionResponse {

    private String title;
    private String content;

    public static SelfIntroductionResponse from(SelfIntroduction selfIntroduction) {
        return SelfIntroductionResponse.builder()
                .title(selfIntroduction.getTitle())
                .content(selfIntroduction.getContent())
                .build();
    }

}
