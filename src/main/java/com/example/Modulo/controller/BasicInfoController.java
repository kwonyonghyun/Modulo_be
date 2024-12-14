package com.example.Modulo.controller;

import com.example.Modulo.dto.request.BasicInfoCreateRequest;
import com.example.Modulo.dto.request.BasicInfoUpdateRequest;
import com.example.Modulo.dto.response.BasicInfoResponse;
import com.example.Modulo.global.response.ResultCode;
import com.example.Modulo.global.response.ResultResponse;
import com.example.Modulo.service.BasicInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/basic-info")
@RequiredArgsConstructor
public class BasicInfoController {
    private final BasicInfoService basicInfoService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResultResponse> createBasicInfo(
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage,
            @RequestPart("request") BasicInfoCreateRequest request) {
        Long basicInfoId = basicInfoService.createBasicInfo(request, profileImage);
        return ResponseEntity.ok(ResultResponse.of(ResultCode.BASIC_INFO_CREATE_SUCCESS, basicInfoId));
    }

    @GetMapping
    public ResponseEntity<ResultResponse> getMyBasicInfos() {
        List<BasicInfoResponse> responses = basicInfoService.getMyBasicInfos();
        return ResponseEntity.ok(ResultResponse.of(ResultCode.BASIC_INFO_GET_SUCCESS, responses));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResultResponse> getBasicInfo(@PathVariable Long id) {
        BasicInfoResponse response = basicInfoService.getBasicInfoById(id);
        return ResponseEntity.ok(ResultResponse.of(ResultCode.BASIC_INFO_GET_SUCCESS, response));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResultResponse> updateBasicInfo(
            @PathVariable Long id,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage,
            @RequestPart("request") BasicInfoUpdateRequest request) {
        basicInfoService.updateBasicInfo(id, request, profileImage);
        return ResponseEntity.ok(ResultResponse.of(ResultCode.BASIC_INFO_UPDATE_SUCCESS));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResultResponse> deleteBasicInfo(@PathVariable Long id) {
        basicInfoService.deleteBasicInfo(id);
        return ResponseEntity.ok(ResultResponse.of(ResultCode.BASIC_INFO_DELETE_SUCCESS));
    }
}