package com.example.Modulo.controller;

import com.example.Modulo.dto.request.ProjectCreateRequest;
import com.example.Modulo.dto.request.ProjectUpdateRequest;
import com.example.Modulo.dto.response.ProjectResponse;
import com.example.Modulo.global.response.ResultCode;
import com.example.Modulo.global.response.ResultResponse;
import com.example.Modulo.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/project")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    public ResponseEntity<ResultResponse> createProject(@RequestBody ProjectCreateRequest request) {
        Long projectId = projectService.createProject(request);
        return ResponseEntity.ok(ResultResponse.of(ResultCode.PROJECT_CREATE_SUCCESS, projectId));
    }

    @GetMapping
    public ResponseEntity<ResultResponse> getMyProjects() {
        List<ProjectResponse> projects = projectService.getMyProjects();
        return ResponseEntity.ok(ResultResponse.of(ResultCode.PROJECT_GET_SUCCESS, projects));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResultResponse> updateProject(
            @PathVariable Long id,
            @RequestBody ProjectUpdateRequest request) {
        projectService.updateProject(id, request);
        return ResponseEntity.ok(ResultResponse.of(ResultCode.PROJECT_UPDATE_SUCCESS));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResultResponse> deleteProject(@PathVariable Long id) {
        projectService.deleteProject(id);
        return ResponseEntity.ok(ResultResponse.of(ResultCode.PROJECT_DELETE_SUCCESS));
    }
}
