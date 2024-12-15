package com.example.Modulo.dto.response;

import com.example.Modulo.domain.SelfIntroduction;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
public class SavedModuleResponse {

    private List<BasicInfoResponse> basicInfos;
    private List<CareerResponse> careers;
    private List<EducationResponse> educations;
    private List<EtcResponse> etcs;
    private List<ProjectResponse> projects;
    private List<SelfIntroductionResponse> selfIntroductions;
}
