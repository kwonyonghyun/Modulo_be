package com.example.Modulo.controller;

import com.example.Modulo.dto.request.UpdateNicknameRequest;
import com.example.Modulo.dto.response.MemberResponse;
import com.example.Modulo.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberController {
    private final MemberService memberService;

    @GetMapping("/{memberId}")
    public ResponseEntity<MemberResponse> getMember(@PathVariable Long memberId) {
        return ResponseEntity.ok(memberService.getMember(memberId));
    }

    @PatchMapping("/{memberId}/nickname")
    public ResponseEntity<MemberResponse> updateNickname(
            @PathVariable Long memberId,
            @Valid @RequestBody UpdateNicknameRequest request) {
        return ResponseEntity.ok(memberService.updateNickname(memberId, request));
    }
}