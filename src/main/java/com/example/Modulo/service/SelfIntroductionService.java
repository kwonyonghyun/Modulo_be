package com.example.Modulo.service;

import com.example.Modulo.domain.SelfIntroduction;
import com.example.Modulo.domain.Member;
import com.example.Modulo.dto.request.SelfIntroductionCreateRequest;
import com.example.Modulo.dto.request.SelfIntroductionUpdateRequest;
import com.example.Modulo.dto.response.SelfIntroductionResponse;
import com.example.Modulo.exception.SelfIntroductionNotFoundException;
import com.example.Modulo.exception.MemberNotFoundException;
import com.example.Modulo.exception.UnauthorizedAccessException;
import com.example.Modulo.repository.SelfIntroductionRepository;
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
public class SelfIntroductionService {

    private final SelfIntroductionRepository selfIntroductionRepository;
    private final MemberRepository memberRepository;

    private Long getCurrentMemberId() {
        return Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @Transactional
    public Long createSelfIntroduction(SelfIntroductionCreateRequest request) {
        Long memberId = getCurrentMemberId();
        Member member = memberRepository.findById(memberId)
                .orElseThrow(MemberNotFoundException::new);

        SelfIntroduction selfIntroduction = SelfIntroduction.builder()
                .member(member)
                .title(request.getTitle())
                .content(request.getContent())
                .build();

        return selfIntroductionRepository.save(selfIntroduction).getId();
    }

    public List<SelfIntroductionResponse> getMySelfIntroductions() {
        Long memberId = getCurrentMemberId();
        List<SelfIntroduction> selfIntroductions = selfIntroductionRepository.findAllByMemberId(memberId);
        return selfIntroductions.stream()
                .map(SelfIntroductionResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateSelfIntroduction(Long selfIntroductionId, SelfIntroductionUpdateRequest request) {
        Long memberId = getCurrentMemberId();
        SelfIntroduction selfIntroduction = getSelfIntroduction(selfIntroductionId);

        validateMemberAccess(selfIntroduction, memberId);

        selfIntroduction.update(
                request.getTitle(),
                request.getContent()
        );
    }

    private SelfIntroduction getSelfIntroduction(Long selfIntroductionId) {
        SelfIntroduction selfIntroduction = selfIntroductionRepository.findById(selfIntroductionId)
                .orElseThrow(SelfIntroductionNotFoundException::new);
        return selfIntroduction;
    }

    @Transactional
    public void deleteSelfIntroduction(Long selfIntroductionId) {
        Long memberId = getCurrentMemberId();
        SelfIntroduction selfIntroduction = getSelfIntroduction(selfIntroductionId);

        validateMemberAccess(selfIntroduction, memberId);

        selfIntroductionRepository.delete(selfIntroduction);
    }

    private void validateMemberAccess(SelfIntroduction selfIntroduction, Long memberId) {
        if (!selfIntroduction.getMember().getId().equals(memberId)) {
            throw new UnauthorizedAccessException();
        }
    }

    public SelfIntroductionResponse getIntroductionById(Long selfIntroductionId) {
        Long memberId = getCurrentMemberId();
        SelfIntroduction selfIntroduction = getSelfIntroduction(selfIntroductionId);

        validateMemberAccess(selfIntroduction, memberId);

        return SelfIntroductionResponse.from(selfIntroduction);
    }
}
