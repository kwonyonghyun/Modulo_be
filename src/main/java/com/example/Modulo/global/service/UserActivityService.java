package com.example.Modulo.global.service;

import com.example.Modulo.domain.DailyActivity;
import com.example.Modulo.repository.DailyActivityRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserActivityService {
    private final RedisTemplate<String, String> redisTemplate;
    private final DailyActivityRepository dailyActivityRepository;
    private static final String DAU_KEY_PREFIX = "dau:";

    @Async
    @Scheduled(cron = "0 0 0 * * *")
    public void writeBackToDatabase() {
        List<String> keys = getAllActiveDayKeys();
        List<CompletableFuture<Void>> futures = keys.stream()
                .map(key -> CompletableFuture.runAsync(() -> processWriteBack(key)))
                .collect(Collectors.toList());

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    }

    private List<String> getAllActiveDayKeys() {
        Set<String> keys = redisTemplate.keys(DAU_KEY_PREFIX + "*");
        return keys != null ? keys.stream().toList() : List.of();
    }

    @Transactional
    public void processWriteBack(String key) {
        try {
            LocalDate date = extractDate(key);
            Set<String> activeUsers = redisTemplate.opsForSet().members(key);

            long activeUserCount = (activeUsers != null) ? activeUsers.size() : 0L;

            DailyActivity activity = DailyActivity.builder()
                    .date(date)
                    .activeUserCount(activeUserCount)
                    .lastUpdated(LocalDateTime.now())
                    .build();

            dailyActivityRepository.save(activity);
            redisTemplate.delete(key);
            log.info("Successfully wrote back data for date: {}", date);
        } catch (Exception e) {
            log.error("Failed to process write back for key: " + key, e);
        }
    }

    private LocalDate extractDate(String key) {
        return LocalDate.parse(key.substring(DAU_KEY_PREFIX.length()));
    }

    public Long getMAU(YearMonth yearMonth) {
        return dailyActivityRepository.findByDateBetween(
                        yearMonth.atDay(1),
                        yearMonth.atEndOfMonth()
                ).stream()
                .mapToLong(DailyActivity::getActiveUserCount)
                .sum();
    }
}