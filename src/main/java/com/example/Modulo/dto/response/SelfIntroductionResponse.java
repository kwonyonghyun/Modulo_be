package com.example.Modulo.dto.response;

import com.example.Modulo.domain.SelfIntroduction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SelfIntroductionResponse implements Serializable {

    private Long id;
    private String title;
    private String content;

    public static SelfIntroductionResponse from(SelfIntroduction selfIntroduction) {
        return SelfIntroductionResponse.builder()
                .id(selfIntroduction.getId())
                .title(selfIntroduction.getTitle())
                .content(selfIntroduction.getContent())
                .build();
    }

}
