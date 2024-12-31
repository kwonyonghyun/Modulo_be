package com.example.Modulo.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RedisServiceTest {

    @InjectMocks
    private RedisService redisService;

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @Test
    @DisplayName("Redis 값 저장 성공")
    void setValues_Success() {
        // given
        String key = "testKey";
        String value = "testValue";
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        // when
        redisService.setValues(key, value);

        // then
        verify(valueOperations).set(key, value, Duration.ofDays(14));
    }

    @Test
    @DisplayName("Redis 값 조회 성공")
    void getValues_Success() {
        // given
        String key = "testKey";
        String value = "testValue";
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(key)).thenReturn(value);

        // when
        String result = redisService.getValues(key);

        // then
        verify(valueOperations).get(key);
        assertThat(result).isEqualTo(value);
    }

    @Test
    @DisplayName("Redis 값 삭제 성공")
    void deleteValues_Success() {
        // given
        String key = "testKey";

        // when
        redisService.deleteValues(key);

        // then
        verify(redisTemplate).delete(key);
    }
}