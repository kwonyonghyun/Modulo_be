package com.example.Modulo.service;

import com.example.Modulo.domain.Etc;
import com.example.Modulo.domain.Member;
import com.example.Modulo.dto.request.EtcCreateRequest;
import com.example.Modulo.dto.request.EtcUpdateRequest;
import com.example.Modulo.dto.response.EtcResponse;
import com.example.Modulo.exception.EtcNotFoundException;
import com.example.Modulo.exception.UnauthorizedAccessException;
import com.example.Modulo.global.enums.EtcType;
import com.example.Modulo.repository.EtcRepository;
import com.example.Modulo.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.lang.reflect.Field;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EtcServiceTest {

    @InjectMocks
    private EtcService etcService;

    @Mock
    private EtcRepository etcRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    private Member member;
    private Etc etc;
    private EtcCreateRequest createRequest;
    private EtcUpdateRequest updateRequest;

    @BeforeEach
    void setUp() throws Exception {
        member = Member.builder()
                .email("test@test.com")
                .name("테스터")
                .build();

        Field memberIdField = member.getClass().getDeclaredField("id");
        memberIdField.setAccessible(true);
        memberIdField.set(member, 1L);

        etc = Etc.builder()
                .member(member)
                .startDate(YearMonth.of(2023, 1))
                .endDate(YearMonth.of(2023, 12))
                .title("SQLD")
                .description("SQL 개발자 자격증")
                .type(EtcType.CERTIFICATE)
                .organization("한국데이터산업진흥원")
                .score("최종합격")
                .build();

        Field etcIdField = etc.getClass().getDeclaredField("id");
        etcIdField.setAccessible(true);
        etcIdField.set(etc, 1L);

        createRequest = new EtcCreateRequest();
        setFieldValue(createRequest, "startDate", YearMonth.of(2023, 1));
        setFieldValue(createRequest, "endDate", YearMonth.of(2023, 12));
        setFieldValue(createRequest, "title", "SQLD");
        setFieldValue(createRequest, "description", "SQL 개발자 자격증");
        setFieldValue(createRequest, "type", EtcType.CERTIFICATE);
        setFieldValue(createRequest, "organization", "한국데이터산업진흥원");
        setFieldValue(createRequest, "score", "최종합격");

        updateRequest = new EtcUpdateRequest();
        setFieldValue(updateRequest, "startDate", YearMonth.of(2023, 1));
        setFieldValue(updateRequest, "endDate", YearMonth.of(2023, 12));
        setFieldValue(updateRequest, "title", "SQLP");
        setFieldValue(updateRequest, "description", "SQL 전문가 자격증");
        setFieldValue(updateRequest, "type", EtcType.CERTIFICATE);
        setFieldValue(updateRequest, "organization", "한국데이터산업진흥원");
        setFieldValue(updateRequest, "score", "최종합격");

        SecurityContextHolder.setContext(securityContext);
        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        lenient().when(authentication.getName()).thenReturn("1");
    }

    private void setFieldValue(Object object, String fieldName, Object value) throws Exception {
        Field field = object.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(object, value);
    }

    @Test
    @DisplayName("기타사항 생성 성공")
    void createEtc_Success() {
        // given
        given(memberRepository.findById(1L)).willReturn(Optional.of(member));
        given(etcRepository.save(any(Etc.class))).willReturn(etc);

        // when
        Long etcId = etcService.createEtc(createRequest);

        // then
        assertThat(etcId).isEqualTo(1L);
        verify(etcRepository).save(any(Etc.class));
    }

    @Test
    @DisplayName("내 기타사항 목록 조회 성공")
    void getMyEtcs_Success() {
        // given
        given(etcRepository.findAllByMemberId(1L)).willReturn(List.of(etc));

        // when
        List<EtcResponse> result = etcService.getMyEtcs();

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo(etc.getTitle());
        assertThat(result.get(0).getType()).isEqualTo(etc.getType());
    }

    @Test
    @DisplayName("기타사항 수정 성공")
    void updateEtc_Success() {
        // given
        given(etcRepository.findById(1L)).willReturn(Optional.of(etc));

        // when
        etcService.updateEtc(1L, updateRequest);

        // then
        assertThat(etc.getTitle()).isEqualTo(updateRequest.getTitle());
        assertThat(etc.getDescription()).isEqualTo(updateRequest.getDescription());
    }

    @Test
    @DisplayName("기타사항 삭제 성공")
    void deleteEtc_Success() {
        // given
        given(etcRepository.findById(1L)).willReturn(Optional.of(etc));

        // when
        etcService.deleteEtc(1L);

        // then
        verify(etcRepository).delete(etc);
    }

    @Test
    @DisplayName("존재하지 않는 기타사항 수정 시 예외 발생")
    void updateEtc_NotFound() {
        // given
        given(etcRepository.findById(1L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> etcService.updateEtc(1L, updateRequest))
                .isInstanceOf(EtcNotFoundException.class);
    }

    @Test
    @DisplayName("권한 없는 기타사항 수정 시 예외 발생")
    void updateEtc_Unauthorized() throws Exception {
        // given
        Member otherMember = Member.builder()
                .email("other@test.com")
                .name("다른사용자")
                .build();

        Field otherMemberIdField = otherMember.getClass().getDeclaredField("id");
        otherMemberIdField.setAccessible(true);
        otherMemberIdField.set(otherMember, 2L);

        Etc otherEtc = Etc.builder()
                .member(otherMember)
                .startDate(YearMonth.of(2023, 1))
                .endDate(YearMonth.of(2023, 12))
                .title("다른 자격증")
                .description("다른 설명")
                .type(EtcType.CERTIFICATE)
                .organization("다른 기관")
                .score("다른 점수")
                .build();

        Field otherEtcIdField = otherEtc.getClass().getDeclaredField("id");
        otherEtcIdField.setAccessible(true);
        otherEtcIdField.set(otherEtc, 1L);

        given(etcRepository.findById(1L)).willReturn(Optional.of(otherEtc));

        // when & then
        assertThatThrownBy(() -> etcService.updateEtc(1L, updateRequest))
                .isInstanceOf(UnauthorizedAccessException.class);
    }
}