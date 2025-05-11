package com.timestamper.blockchain_analytics.dto.heatmap.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class DailyInfoResponseDTO {
    private String day;
    private List<TimeBlock> timeBlocks;

    @Data
    @AllArgsConstructor
    public static class TimeBlock {
        private String range;
        private int txCount;
    }
}
