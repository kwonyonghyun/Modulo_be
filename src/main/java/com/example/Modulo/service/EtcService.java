package com.example.Modulo.service;

import com.example.Modulo.domain.Etc;
import com.example.Modulo.domain.Member;
import com.example.Modulo.dto.request.EtcCreateRequest;
import com.example.Modulo.dto.request.EtcUpdateRequest;
import com.example.Modulo.dto.response.EtcResponse;
import com.example.Modulo.exception.EtcNotFoundException;
import com.example.Modulo.exception.MemberNotFoundException;
import com.example.Modulo.exception.UnauthorizedAccessException;
import com.example.Modulo.repository.EtcRepository;
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
public class EtcService {

    private final EtcRepository etcRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public Long createEtc(EtcCreateRequest request) {
        Member member = getMemberFromToken();

        Etc etc = Etc.builder()
                .member(member)
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .title(request.getTitle())
                .description(request.getDescription())
                .type(request.getType())
                .organization(request.getOrganization())
                .score(request.getScore())
                .build();

        return etcRepository.save(etc).getId();
    }

    public List<EtcResponse> getMyEtcs() {
        Long memberId = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());
        return etcRepository.findAllByMemberId(memberId).stream()
                .map(EtcResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateEtc(Long id, EtcUpdateRequest request) {
        Etc etc = etcRepository.findById(id)
                .orElseThrow(EtcNotFoundException::new);

        validateMemberAccess(etc);

        etc.update(
                request.getStartDate(),
                request.getEndDate(),
                request.getTitle(),
                request.getDescription(),
                request.getType(),
                request.getOrganization(),
                request.getScore()
        );
    }

    @Transactional
    public void deleteEtc(Long id) {
        Etc etc = etcRepository.findById(id)
                .orElseThrow(EtcNotFoundException::new);

        validateMemberAccess(etc);

        etcRepository.delete(etc);
    }

    private Member getMemberFromToken() {
        Long memberId = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());
        return memberRepository.findById(memberId)
                .orElseThrow(MemberNotFoundException::new);
    }

    private void validateMemberAccess(Etc etc) {
        Long memberId = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());
        if (!etc.getMember().getId().equals(memberId)) {
            throw new UnauthorizedAccessException();
        }
    }
}