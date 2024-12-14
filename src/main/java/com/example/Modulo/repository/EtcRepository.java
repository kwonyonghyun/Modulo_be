package com.example.Modulo.repository;

import com.example.Modulo.domain.Etc;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EtcRepository extends JpaRepository<Etc, Long> {
    List<Etc> findAllByMemberId(Long memberId);
} 