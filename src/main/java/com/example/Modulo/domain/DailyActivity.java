package com.example.Modulo.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "daily_activity")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class DailyActivity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private LocalDate date;

    private Long activeUserCount;

    private LocalDateTime lastUpdated;

    public void incrementUserCount() {
        this.activeUserCount++;
    }
}