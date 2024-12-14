package com.example.Modulo.repository;

import com.example.Modulo.domain.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findAllByMemberId(Long memberId);
} 