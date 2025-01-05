package com.example.Modulo.controller;

import com.example.Modulo.dto.request.CareerCreateRequest;
import com.example.Modulo.dto.request.CareerUpdateRequest;
import com.example.Modulo.dto.response.CareerResponse;
import com.example.Modulo.global.annotation.TrackUserActivity;
import com.example.Modulo.global.response.ResultCode;
import com.example.Modulo.global.response.ResultResponse;
import com.example.Modulo.service.CareerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/career")
@RequiredArgsConstructor
@TrackUserActivity
public class CareerController {

    private final CareerService careerService;

    @PostMapping
    public ResponseEntity<ResultResponse> createCareer(@RequestBody CareerCreateRequest request) {
        Long careerId = careerService.createCareer(request);
        return ResponseEntity.ok(ResultResponse.of(ResultCode.CAREER_CREATE_SUCCESS, careerId));
    }

    @GetMapping
    public ResponseEntity<ResultResponse> getMyCareers() {
        List<CareerResponse> careers = careerService.getMyCareers();
        return ResponseEntity.ok(ResultResponse.of(ResultCode.CAREER_GET_SUCCESS, careers));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResultResponse> updateCareer(
            @PathVariable Long id,
            @RequestBody CareerUpdateRequest request) {
        careerService.updateCareer(id, request);
        return ResponseEntity.ok(ResultResponse.of(ResultCode.CAREER_UPDATE_SUCCESS));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResultResponse> deleteCareer(@PathVariable Long id) {
        careerService.deleteCareer(id);
        return ResponseEntity.ok(ResultResponse.of(ResultCode.CAREER_DELETE_SUCCESS));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResultResponse> getCareer(@PathVariable Long id) {
        CareerResponse careerResponse = careerService.getCareerById(id);
        return ResponseEntity.ok(ResultResponse.of(ResultCode.CAREER_GET_SUCCESS, careerResponse));
    }
}