package com.example.Modulo.service;

import com.example.Modulo.domain.Member;
import com.example.Modulo.dto.request.UpdateNicknameRequest;
import com.example.Modulo.dto.response.MemberResponse;
import com.example.Modulo.exception.MemberNotFoundException;
import com.example.Modulo.exception.UnauthorizedAccessException;
import com.example.Modulo.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {
    private final MemberRepository memberRepository;

    public MemberResponse getMember(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(MemberNotFoundException::new);
        return MemberResponse.from(member);
    }

    @Transactional
    public MemberResponse updateNickname(Long memberId, UpdateNicknameRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(MemberNotFoundException::new);
        if(!member.getId().equals(memberId)){
            throw new UnauthorizedAccessException();
        }
        member.updateNickname(request.getNickname());
        return MemberResponse.from(member);
    }
}