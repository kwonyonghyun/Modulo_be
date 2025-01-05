package com.example.Modulo.controller;

import com.example.Modulo.dto.response.SavedModuleResponse;
import com.example.Modulo.global.annotation.TrackUserActivity;
import com.example.Modulo.global.response.ResultCode;
import com.example.Modulo.global.response.ResultResponse;
import com.example.Modulo.service.SavedModuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/saved-modules")
@TrackUserActivity
public class SavedModuleController{

    private final SavedModuleService savedModuleService;

    @GetMapping
    public ResponseEntity<ResultResponse> getMySavedModules(){
        SavedModuleResponse savedModuleResponse = savedModuleService.getMySavedModules();
        return ResponseEntity.ok(ResultResponse.of(ResultCode.SAVED_MODULE_GET_SUCCESS,savedModuleResponse));
    }
}