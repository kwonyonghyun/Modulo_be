package com.example.Modulo.repository;

import com.example.Modulo.domain.SelfIntroduction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SelfIntroductionRepository extends JpaRepository<SelfIntroduction, Long> {

    List<SelfIntroduction> findAllByMemberId(Long memberId);
}
