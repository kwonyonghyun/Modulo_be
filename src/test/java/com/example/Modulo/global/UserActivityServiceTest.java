package com.example.Modulo.global;

import com.example.Modulo.domain.DailyActivity;
import com.example.Modulo.global.service.UserActivityService;
import com.example.Modulo.repository.DailyActivityRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserActivityServiceTest {
    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private DailyActivityRepository dailyActivityRepository;

    @Mock
    private SetOperations<String, String> setOperations;

    @InjectMocks
    private UserActivityService userActivityService;

    @Test
    @DisplayName("Redis의 사용자 활동 데이터가 DB에 정상적으로 저장되어야 한다")
    void writeBackToDatabase() {
        // given
        String today = LocalDate.now().toString();
        String key = "dau:" + today;
        Set<String> activeUsers = Set.of("1", "2", "3");

        when(redisTemplate.opsForSet()).thenReturn(setOperations);
        when(redisTemplate.keys(anyString())).thenReturn(Set.of(key));
        when(setOperations.members(key)).thenReturn(activeUsers);

        // when
        userActivityService.writeBackToDatabase();

        // then
        verify(dailyActivityRepository).save(argThat(activity ->
                activity.getDate().equals(LocalDate.now()) &&
                        activity.getActiveUserCount() == 3L
        ));
    }

    @Test
    @DisplayName("MAU가 정상적으로 계산되어야 한다")
    void calculateMAU() {
        // given
        YearMonth yearMonth = YearMonth.now();
        List<DailyActivity> activities = Arrays.asList(
                DailyActivity.builder().activeUserCount(10L).date(yearMonth.atDay(1)).build(),
                DailyActivity.builder().activeUserCount(20L).date(yearMonth.atDay(15)).build()
        );
        when(dailyActivityRepository.findByDateBetween(any(), any())).thenReturn(activities);

        // when
        Long mau = userActivityService.getMAU(yearMonth);

        // then
        assertThat(mau).isEqualTo(30L);
    }
}