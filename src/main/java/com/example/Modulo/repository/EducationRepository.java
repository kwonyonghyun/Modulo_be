package com.example.Modulo.repository;

import com.example.Modulo.domain.Education;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EducationRepository extends JpaRepository<Education, Long> {
    List<Education> findAllByMemberId(Long memberId);
}