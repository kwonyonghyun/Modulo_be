package com.example.Modulo.controller;

import com.example.Modulo.dto.request.ResumeCreateRequest;
import com.example.Modulo.dto.request.ResumeUpdateRequest;
import com.example.Modulo.dto.response.ResumeDetailResponse;
import com.example.Modulo.dto.response.ResumeResponse;
import com.example.Modulo.global.annotation.TrackUserActivity;
import com.example.Modulo.global.response.ResultCode;
import com.example.Modulo.global.response.ResultResponse;
import com.example.Modulo.service.ResumeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/resume")
@RequiredArgsConstructor
@TrackUserActivity
public class ResumeController {

    private final ResumeService resumeService;

    @PostMapping
    public ResponseEntity<ResultResponse> createResume(@RequestBody ResumeCreateRequest request) {
        Long resumeId = resumeService.createResume(request);
        return ResponseEntity.ok(ResultResponse.of(ResultCode.RESUME_CREATE_SUCCESS, resumeId));
    }

    @GetMapping
    public ResponseEntity<ResultResponse> getMyResumes() {
        List<ResumeResponse> resumes = resumeService.getMyResumes();
        return ResponseEntity.ok(ResultResponse.of(ResultCode.RESUME_GET_SUCCESS, resumes));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResultResponse> updateResume(
            @PathVariable Long id,
            @RequestBody ResumeUpdateRequest request) {
        resumeService.updateResume(id, request);
        return ResponseEntity.ok(ResultResponse.of(ResultCode.RESUME_UPDATE_SUCCESS));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResultResponse> deleteResume(@PathVariable Long id) {
        resumeService.deleteResume(id);
        return ResponseEntity.ok(ResultResponse.of(ResultCode.RESUME_DELETE_SUCCESS));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResultResponse> getResume(@PathVariable Long id) {
        ResumeResponse resumeResponse = resumeService.getResumeById(id);
        return ResponseEntity.ok(ResultResponse.of(ResultCode.RESUME_GET_SUCCESS, resumeResponse));
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<ResultResponse> getResumeDetail(@PathVariable Long id) {
        ResumeDetailResponse resumeDetailResponse = resumeService.getResumeDetailById(id);
        return ResponseEntity.ok(ResultResponse.of(ResultCode.RESUME_GET_SUCCESS, resumeDetailResponse));
    }
}