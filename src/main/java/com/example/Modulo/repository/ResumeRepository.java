package com.example.Modulo.repository;

import com.example.Modulo.domain.Resume;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ResumeRepository extends JpaRepository<Resume, Long> {
    List<Resume> findAllByMemberId(Long memberId);
}