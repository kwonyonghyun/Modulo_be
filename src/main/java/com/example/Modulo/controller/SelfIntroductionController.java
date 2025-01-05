package com.example.Modulo.controller;

import com.example.Modulo.dto.request.SelfIntroductionCreateRequest;
import com.example.Modulo.dto.request.SelfIntroductionUpdateRequest;
import com.example.Modulo.dto.response.SelfIntroductionResponse;
import com.example.Modulo.global.annotation.TrackUserActivity;
import com.example.Modulo.global.response.ResultCode;
import com.example.Modulo.global.response.ResultResponse;
import com.example.Modulo.service.SelfIntroductionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/self-introduction")
@RequiredArgsConstructor
@TrackUserActivity
public class SelfIntroductionController {

    private final SelfIntroductionService selfIntroductionService;

    @PostMapping
    public ResponseEntity<ResultResponse> createSelfIntroduction(@RequestBody SelfIntroductionCreateRequest request) {
        Long selfIntroductionId = selfIntroductionService.createSelfIntroduction(request);
        return ResponseEntity.ok(ResultResponse.of(ResultCode.SELF_INTRODUCTION_CREATE_SUCCESS, selfIntroductionId));
    }

    @GetMapping
    public ResponseEntity<ResultResponse> getMySelfIntroductions() {
        List<SelfIntroductionResponse> selfIntroductions = selfIntroductionService.getMySelfIntroductions();
        return ResponseEntity.ok(ResultResponse.of(ResultCode.SELF_INTRODUCTION_GET_SUCCESS, selfIntroductions));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResultResponse> getSelfIntroductions(@PathVariable Long id) {
        SelfIntroductionResponse selfIntroductionResponse = selfIntroductionService.getIntroductionById(id);
        return ResponseEntity.ok(ResultResponse.of(ResultCode.SELF_INTRODUCTION_GET_SUCCESS, selfIntroductionResponse));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResultResponse> updateSelfIntroduction(
            @PathVariable Long id,
            @RequestBody SelfIntroductionUpdateRequest request) {
        selfIntroductionService.updateSelfIntroduction(id, request);
        return ResponseEntity.ok(ResultResponse.of(ResultCode.SELF_INTRODUCTION_UPDATE_SUCCESS));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResultResponse> deleteSelfIntroduction(@PathVariable Long id) {
        selfIntroductionService.deleteSelfIntroduction(id);
        return ResponseEntity.ok(ResultResponse.of(ResultCode.SELF_INTRODUCTION_DELETE_SUCCESS));
    }
}
