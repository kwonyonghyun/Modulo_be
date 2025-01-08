package com.example.Modulo.dto.response;

import com.example.Modulo.domain.SelfIntroduction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SavedModuleResponse implements Serializable {

    private List<BasicInfoResponse> basicInfos;
    private List<CareerResponse> careers;
    private List<EducationResponse> educations;
    private List<EtcResponse> etcs;
    private List<ProjectResponse> projects;
    private List<SelfIntroductionResponse> selfIntroductions;
}
