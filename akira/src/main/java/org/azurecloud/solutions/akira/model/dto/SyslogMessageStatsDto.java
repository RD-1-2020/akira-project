package org.azurecloud.solutions.akira.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SyslogMessageStatsDto {
    private List<HourlyStats> hourlyStats;
    private double mean;
    private double standardDeviation;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HourlyStats {
        private int hour;
        private long count;
        @JsonProperty("isAnomaly")
        private boolean isAnomaly;
    }
}

