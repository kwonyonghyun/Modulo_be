package com.example.Modulo.controller;

import com.example.Modulo.dto.request.EducationCreateRequest;
import com.example.Modulo.dto.request.EducationUpdateRequest;
import com.example.Modulo.dto.response.EducationResponse;
import com.example.Modulo.global.annotation.TrackUserActivity;
import com.example.Modulo.global.response.ResultCode;
import com.example.Modulo.global.response.ResultResponse;
import com.example.Modulo.service.EducationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/education")
@RequiredArgsConstructor
@TrackUserActivity
public class EducationController {

    private final EducationService educationService;

    @PostMapping
    public ResponseEntity<ResultResponse> createEducation(@RequestBody EducationCreateRequest request) {
        Long educationId = educationService.createEducation(request);
        return ResponseEntity.ok(ResultResponse.of(ResultCode.EDUCATION_CREATE_SUCCESS, educationId));
    }

    @GetMapping
    public ResponseEntity<ResultResponse> getMyEducations() {
        List<EducationResponse> educations = educationService.getMyEducations();
        return ResponseEntity.ok(ResultResponse.of(ResultCode.EDUCATION_GET_SUCCESS, educations));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResultResponse> updateEducation(
            @PathVariable Long id,
            @RequestBody EducationUpdateRequest request) {
        educationService.updateEducation(id, request);
        return ResponseEntity.ok(ResultResponse.of(ResultCode.EDUCATION_UPDATE_SUCCESS));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResultResponse> deleteEducation(@PathVariable Long id) {
        educationService.deleteEducation(id);
        return ResponseEntity.ok(ResultResponse.of(ResultCode.EDUCATION_DELETE_SUCCESS));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResultResponse> getEducation(@PathVariable Long id) {
        EducationResponse educationResponse = educationService.getEducationById(id);
        return ResponseEntity.ok(ResultResponse.of(ResultCode.EDUCATION_GET_SUCCESS, educationResponse));
    }
}