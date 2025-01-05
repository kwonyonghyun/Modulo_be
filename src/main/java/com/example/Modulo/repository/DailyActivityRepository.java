package com.example.Modulo.repository;

import com.example.Modulo.domain.DailyActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DailyActivityRepository extends JpaRepository<DailyActivity, Long> {
    Optional<DailyActivity> findByDate(LocalDate date);

    List<DailyActivity> findByDateBetween(LocalDate startDate, LocalDate endDate);
}
