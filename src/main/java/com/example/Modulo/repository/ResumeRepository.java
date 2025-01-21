package com.example.Modulo.repository;

import com.example.Modulo.domain.Resume;
import com.example.Modulo.domain.ResumeSection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ResumeRepository extends JpaRepository<Resume, Long> {
    List<Resume> findAllByMemberId(Long memberId);

    @Query("SELECT DISTINCT r FROM Resume r " +
            "LEFT JOIN FETCH r.sections " +
            "WHERE r.id = :resumeId")
    Optional<Resume> findByIdWithSections(@Param("resumeId") Long resumeId);

    @Query("SELECT DISTINCT s FROM ResumeSection s " +
            "LEFT JOIN FETCH s.contents " +
            "WHERE s.resume.id = :resumeId")
    List<ResumeSection> findSectionsByResumeIdWithContents(@Param("resumeId") Long resumeId);

    @Query("SELECT DISTINCT r FROM Resume r " +
            "LEFT JOIN FETCH r.sections " +
            "WHERE r.member.id = :memberId")
    List<Resume> findAllByMemberIdWithSections(@Param("memberId") Long memberId);

    @Query("SELECT DISTINCT s FROM ResumeSection s " +
            "LEFT JOIN FETCH s.contents " +
            "WHERE s.resume.id IN :resumeIds")
    List<ResumeSection> findSectionsByResumeIdsWithContents(@Param("resumeIds") List<Long> resumeIds);
}