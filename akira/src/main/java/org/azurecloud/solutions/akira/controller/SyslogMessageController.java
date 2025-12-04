package org.azurecloud.solutions.akira.controller;

import lombok.RequiredArgsConstructor;
import org.azurecloud.solutions.akira.controller.config.Endpoints;
import org.azurecloud.solutions.akira.model.dto.SyslogMessageStatsDto;
import org.azurecloud.solutions.akira.model.entity.SyslogMessage;
import org.azurecloud.solutions.akira.repository.SyslogMessageRepository;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(Endpoints.SYSLOG_MESSAGES)
@RequiredArgsConstructor
public class SyslogMessageController {

    private final SyslogMessageRepository syslogMessageRepository;

    @GetMapping
    public List<SyslogMessage> getMessagesBySourceId(
            @RequestParam Long sourceId,
            @RequestParam(required = false) Long startDate,
            @RequestParam(required = false) Long endDate) {
        LocalDateTime startDateTime = null;
        LocalDateTime endDateTime = null;
        
        if (startDate != null) {
            startDateTime = Instant.ofEpochMilli(startDate).atZone(ZoneId.systemDefault()).toLocalDateTime();
        }
        if (endDate != null) {
            endDateTime = Instant.ofEpochMilli(endDate).atZone(ZoneId.systemDefault()).toLocalDateTime();
        }
        
        if (startDateTime != null && endDateTime != null) {
            return syslogMessageRepository.findBySourceIdAndDateRangeOrderByReceivedAtDesc(sourceId, startDateTime, endDateTime);
        } else if (startDateTime != null) {
            return syslogMessageRepository.findBySourceIdAndStartDateOrderByReceivedAtDesc(sourceId, startDateTime);
        } else if (endDateTime != null) {
            return syslogMessageRepository.findBySourceIdAndEndDateOrderByReceivedAtDesc(sourceId, endDateTime);
        } else {
            return syslogMessageRepository.findBySourceIdOrderByReceivedAtDesc(sourceId);
        }
    }

    @GetMapping("/stats")
    public SyslogMessageStatsDto getStats(
            @RequestParam Long sourceId,
            @RequestParam Long startDate,
            @RequestParam Long endDate) {
        LocalDateTime startDateTime = Instant.ofEpochMilli(startDate).atZone(ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime endDateTime = Instant.ofEpochMilli(endDate).atZone(ZoneId.systemDefault()).toLocalDateTime();
        List<Object[]> results = syslogMessageRepository.countByHourAndSourceIdInRange(sourceId, startDateTime, endDateTime);

        // Создаём массив для всех 24 часов
        long[] hourlyCounts = new long[24];
        for (Object[] result : results) {
            int hour = ((Number) result[0]).intValue();
            long count = ((Number) result[1]).longValue();
            if (hour >= 0 && hour < 24) {
                hourlyCounts[hour] = count;
            }
        }

        // Вычисляем среднее и стандартное отклонение
        double mean = calculateMean(hourlyCounts);
        double standardDeviation = calculateStandardDeviation(hourlyCounts, mean);

        // Создаём список статистики по часам
        List<SyslogMessageStatsDto.HourlyStats> hourlyStats = new ArrayList<>();
        for (int hour = 0; hour < 24; hour++) {
            long count = hourlyCounts[hour];
            // Аномалия определяется как отклонение больше 3 сигм, но только если стандартное отклонение > 0
            boolean isAnomaly = standardDeviation > 0 && Math.abs(count - mean) > 3 * standardDeviation;
            hourlyStats.add(new SyslogMessageStatsDto.HourlyStats(hour, count, isAnomaly));
        }

        return new SyslogMessageStatsDto(hourlyStats, mean, standardDeviation);
    }

    private double calculateMean(long[] values) {
        long sum = 0;
        for (long value : values) {
            sum += value;
        }
        return values.length > 0 ? (double) sum / values.length : 0.0;
    }

    private double calculateStandardDeviation(long[] values, double mean) {
        if (values.length == 0) {
            return 0.0;
        }
        double sumSquaredDiff = 0.0;
        for (long value : values) {
            double diff = value - mean;
            sumSquaredDiff += diff * diff;
        }
        return Math.sqrt(sumSquaredDiff / values.length);
    }
}

