package com.example.Modulo.service;

import com.example.Modulo.domain.*;
import com.example.Modulo.global.enums.ResumeTheme;
import com.example.Modulo.global.enums.SectionType;
import com.example.Modulo.repository.MemberRepository;
import com.example.Modulo.repository.ResumeRepository;
import jakarta.persistence.EntityManager;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class ResumeServicePerformanceTest {

    @Autowired
    private ResumeRepository resumeRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private EntityManager em;

    private Member testMember;
    private Resume testResume;
    private Statistics statistics;

    @BeforeEach
    void setUp() {
        SessionFactory sessionFactory = em.getEntityManagerFactory().unwrap(SessionFactory.class);
        sessionFactory.getStatistics().setStatisticsEnabled(true);
        statistics = sessionFactory.getStatistics();

        testMember = Member.builder()
                .email("test@test.com")
                .name("테스터")
                .build();
        memberRepository.save(testMember);

        testResume = createTestResume();
        resumeRepository.save(testResume);

        em.flush();
        em.clear();
    }

    private Resume createTestResume() {
        Resume resume = Resume.builder()
                .member(testMember)
                .title("Test Resume")
                .theme(ResumeTheme.BASIC)
                .build();

        List<ResumeSection> sections = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            ResumeSection section = ResumeSection.builder()
                    .resume(resume)
                    .orderIndex(i)
                    .topMargin(10)
                    .sectionType(SectionType.values()[i % SectionType.values().length])
                    .build();

            List<SectionContent> contents = new ArrayList<>();
            for (int j = 0; j < 3; j++) {
                contents.add(SectionContent.builder()
                        .resumeSection(section)
                        .orderIndex(j)
                        .topMargin(5)
                        .contentId((long) (i * 3 + j + 1))
                        .build());
            }
            section.updateContents(contents);
            sections.add(section);
        }
        resume.updateSections(sections);
        return resume;
    }

    @Test
    @DisplayName("단일 이력서 조회 성능 비교")
    void comparePerformance_FindById() {
        // Given
        Long resumeId = testResume.getId();

        // When - N+1 문제가 있는 기존 조회
        statistics.clear();
        long startTime1 = System.nanoTime();
        Resume resume1 = resumeRepository.findById(resumeId).orElseThrow();
        resume1.getSections().forEach(section ->
                section.getContents().size());
        long endTime1 = System.nanoTime();
        long queryCount1 = statistics.getPrepareStatementCount();

        em.clear();

        // When - 두 번의 쿼리로 나누어 조회
        statistics.clear();
        long startTime2 = System.nanoTime();
        Resume resume2 = resumeRepository.findByIdWithSections(resumeId).orElseThrow();
        List<ResumeSection> sections = resumeRepository.findSectionsByResumeIdWithContents(resumeId);
        resume2.updateSections(sections);
        resume2.getSections().forEach(section ->
                section.getContents().size());
        long endTime2 = System.nanoTime();
        long queryCount2 = statistics.getPrepareStatementCount();

        // Then
        System.out.println("=== 단일 이력서 조회 성능 테스트 ===");
        System.out.println("기존 방식 실행 시간: " + (endTime1 - startTime1) / 1_000_000.0 + "ms");
        System.out.println("기존 방식 쿼리 수: " + queryCount1);
        System.out.println("분리 쿼리 실행 시간: " + (endTime2 - startTime2) / 1_000_000.0 + "ms");
        System.out.println("분리 쿼리 수: " + queryCount2);

        assertThat(queryCount2).isLessThan(queryCount1);
    }

    @Test
    @DisplayName("회원별 이력서 목록 조회 성능 비교")
    void comparePerformance_FindAllByMemberId() {
        // Given
        Long memberId = testMember.getId();

        // When - N+1 문제가 있는 기존 조회
        statistics.clear();
        long startTime1 = System.nanoTime();
        List<Resume> resumes1 = resumeRepository.findAllByMemberId(memberId);
        resumes1.forEach(resume -> resume.getSections().forEach(section ->
                section.getContents().size()));
        long endTime1 = System.nanoTime();
        long queryCount1 = statistics.getPrepareStatementCount();

        em.clear();

        // When - 두 번의 쿼리로 나누어 조회
        statistics.clear();
        long startTime2 = System.nanoTime();
        List<Resume> resumes2 = resumeRepository.findAllByMemberIdWithSections(memberId);
        List<Long> resumeIds = resumes2.stream()
                .map(Resume::getId)
                .collect(Collectors.toList());
        List<ResumeSection> sectionsWithContents = resumeRepository.findSectionsByResumeIdsWithContents(resumeIds);

        Map<Long, List<ResumeSection>> sectionsByResumeId = sectionsWithContents.stream()
                .collect(Collectors.groupingBy(section -> section.getResume().getId()));

        resumes2.forEach(resume -> {
            List<ResumeSection> sections = sectionsByResumeId.getOrDefault(resume.getId(), new ArrayList<>());
            resume.updateSections(sections);
        });

        resumes2.forEach(resume -> resume.getSections().forEach(section ->
                section.getContents().size()));
        long endTime2 = System.nanoTime();
        long queryCount2 = statistics.getPrepareStatementCount();

        // Then
        System.out.println("=== 회원별 이력서 목록 조회 성능 테스트 ===");
        System.out.println("기존 방식 실행 시간: " + (endTime1 - startTime1) / 1_000_000.0 + "ms");
        System.out.println("기존 방식 쿼리 수: " + queryCount1);
        System.out.println("분리 쿼리 실행 시간: " + (endTime2 - startTime2) / 1_000_000.0 + "ms");
        System.out.println("분리 쿼리 수: " + queryCount2);

        assertThat(queryCount2).isLessThan(queryCount1);
    }
}