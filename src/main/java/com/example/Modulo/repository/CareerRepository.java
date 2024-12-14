package com.example.Modulo.repository;

import com.example.Modulo.domain.Career;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CareerRepository extends JpaRepository<Career, Long> {
    List<Career> findAllByMemberId(Long memberId);
}