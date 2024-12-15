package com.example.Modulo.service;

import com.example.Modulo.dto.response.SavedModuleResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SavedModuleService {

    private final BasicInfoService basicInfoService;
    private final CareerService careerService;
    private final EducationService educationService;
    private final EtcService etcService;
    private final SelfIntroductionService selfIntroductionService;
    private final ProjectService projectService;

    public SavedModuleResponse getMySavedModules(){
        return SavedModuleResponse.builder()
                .basicInfos(basicInfoService.getMyBasicInfos())
                .careers(careerService.getMyCareers())
                .educations(educationService.getMyEducations())
                .etcs(etcService.getMyEtcs())
                .selfIntroductions(selfIntroductionService.getMySelfIntroductions())
                .projects(projectService.getMyProjects())
                .build();
    }

}
