package com.example.Modulo.repository;

import com.example.Modulo.domain.BasicInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BasicInfoRepository extends JpaRepository<BasicInfo, Long> {
    List<BasicInfo> findAllByMemberId(Long memberId);
}