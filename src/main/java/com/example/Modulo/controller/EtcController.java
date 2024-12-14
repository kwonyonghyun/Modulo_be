package com.example.Modulo.controller;

import com.example.Modulo.dto.request.EtcCreateRequest;
import com.example.Modulo.dto.request.EtcUpdateRequest;
import com.example.Modulo.dto.response.EtcResponse;
import com.example.Modulo.global.response.ResultCode;
import com.example.Modulo.global.response.ResultResponse;
import com.example.Modulo.service.EtcService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/etc")
@RequiredArgsConstructor
public class EtcController {

    private final EtcService etcService;

    @PostMapping
    public ResponseEntity<ResultResponse> createEtc(@RequestBody EtcCreateRequest request) {
        Long etcId = etcService.createEtc(request);
        return ResponseEntity.ok(ResultResponse.of(ResultCode.ETC_CREATE_SUCCESS, etcId));
    }

    @GetMapping
    public ResponseEntity<ResultResponse> getMyEtcs() {
        List<EtcResponse> etcs = etcService.getMyEtcs();
        return ResponseEntity.ok(ResultResponse.of(ResultCode.ETC_GET_SUCCESS, etcs));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResultResponse> updateEtc(
            @PathVariable Long id,
            @RequestBody EtcUpdateRequest request) {
        etcService.updateEtc(id, request);
        return ResponseEntity.ok(ResultResponse.of(ResultCode.ETC_UPDATE_SUCCESS));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResultResponse> deleteEtc(@PathVariable Long id) {
        etcService.deleteEtc(id);
        return ResponseEntity.ok(ResultResponse.of(ResultCode.ETC_DELETE_SUCCESS));
    }
} 