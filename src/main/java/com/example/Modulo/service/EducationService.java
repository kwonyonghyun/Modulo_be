package com.example.Modulo.service;

import com.example.Modulo.domain.Education;
import com.example.Modulo.domain.Member;
import com.example.Modulo.dto.request.EducationCreateRequest;
import com.example.Modulo.dto.request.EducationUpdateRequest;
import com.example.Modulo.dto.response.EducationResponse;
import com.example.Modulo.exception.EducationNotFoundException;
import com.example.Modulo.exception.MemberNotFoundException;
import com.example.Modulo.exception.UnauthorizedAccessException;
import com.example.Modulo.repository.EducationRepository;
import com.example.Modulo.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EducationService {

    private final EducationRepository educationRepository;
    private final MemberRepository memberRepository;

    private Long getCurrentMemberId() {
        return Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @Transactional
    public Long createEducation(EducationCreateRequest request) {
        Long memberId = getCurrentMemberId();
        Member member = memberRepository.findById(memberId)
                .orElseThrow(MemberNotFoundException::new);

        Education education = Education.builder()
                .member(member)
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .school(request.getSchool())
                .major(request.getMajor())
                .educationLevel(request.getEducationLevel())
                .build();

        return educationRepository.save(education).getId();
    }

    public List<EducationResponse> getMyEducations() {
        Long memberId = getCurrentMemberId();
        List<Education> educations = educationRepository.findAllByMemberId(memberId);
        return educations.stream()
                .map(EducationResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateEducation(Long educationId, EducationUpdateRequest request) {
        Long memberId = getCurrentMemberId();
        Education education = getEducation(educationId);

        validateMemberAccess(education, memberId);

        education.update(
                request.getStartDate(),
                request.getEndDate(),
                request.getSchool(),
                request.getMajor(),
                request.getEducationLevel()
        );
    }

    private Education getEducation(Long educationId) {
        Education education = educationRepository.findById(educationId)
                .orElseThrow(EducationNotFoundException::new);
        return education;
    }

    public EducationResponse getEducationById(Long educationId) {
        Education education = getEducation(educationId);

        validateMemberAccess(education, getCurrentMemberId());

        return EducationResponse.from(education);
    }

    @Transactional
    public void deleteEducation(Long educationId) {
        Long memberId = getCurrentMemberId();
        Education education = getEducation(educationId);

        validateMemberAccess(education, memberId);

        educationRepository.delete(education);
    }

    private void validateMemberAccess(Education education, Long memberId) {
        if (!education.getMember().getId().equals(memberId)) {
            throw new UnauthorizedAccessException();
        }
    }
}