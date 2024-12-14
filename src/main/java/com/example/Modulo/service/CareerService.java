package com.example.Modulo.service;

import com.example.Modulo.domain.Career;
import com.example.Modulo.domain.Member;
import com.example.Modulo.dto.request.CareerCreateRequest;
import com.example.Modulo.dto.request.CareerUpdateRequest;
import com.example.Modulo.dto.response.CareerResponse;
import com.example.Modulo.exception.CareerNotFoundException;
import com.example.Modulo.exception.MemberNotFoundException;
import com.example.Modulo.exception.UnauthorizedAccessException;
import com.example.Modulo.repository.CareerRepository;
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
public class CareerService {

    private final CareerRepository careerRepository;
    private final MemberRepository memberRepository;

    private Long getCurrentMemberId() {
        return Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @Transactional
    public Long createCareer(CareerCreateRequest request) {
        Long memberId = getCurrentMemberId();
        Member member = memberRepository.findById(memberId)
                .orElseThrow(MemberNotFoundException::new);

        Career career = Career.builder()
                .member(member)
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .companyName(request.getCompanyName())
                .companyDescription(request.getCompanyDescription())
                .position(request.getPosition())
                .techStack(request.getTechStack())
                .achievements(request.getAchievements())
                .build();

        return careerRepository.save(career).getId();
    }

    public List<CareerResponse> getMyCareers() {
        Long memberId = getCurrentMemberId();
        List<Career> careers = careerRepository.findAllByMemberId(memberId);
        return careers.stream()
                .map(CareerResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateCareer(Long careerId, CareerUpdateRequest request) {
        Long memberId = getCurrentMemberId();
        Career career = careerRepository.findById(careerId)
                .orElseThrow(CareerNotFoundException::new);

        validateMemberAccess(career, memberId);

        career.update(
                request.getStartDate(),
                request.getEndDate(),
                request.getCompanyName(),
                request.getCompanyDescription(),
                request.getPosition(),
                request.getTechStack(),
                request.getAchievements()
        );
    }

    @Transactional
    public void deleteCareer(Long careerId) {
        Long memberId = getCurrentMemberId();
        Career career = careerRepository.findById(careerId)
                .orElseThrow(CareerNotFoundException::new);

        validateMemberAccess(career, memberId);

        careerRepository.delete(career);
    }

    public CareerResponse getCareerById(Long careerId) {
        Long memberId = getCurrentMemberId();
        Career career = careerRepository.findById(careerId)
                .orElseThrow(CareerNotFoundException::new);

        validateMemberAccess(career, memberId);

        return CareerResponse.from(career);
    }

    private void validateMemberAccess(Career career, Long memberId) {
        if (!career.getMember().getId().equals(memberId)) {
            throw new UnauthorizedAccessException();
        }
    }
}
