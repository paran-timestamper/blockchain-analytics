package com.timestamper.blockchain_analytics.dto.heatmap.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
public class CompareMonthlyResponseDTO {

    private List<String> tokens;
    private List<CompareMonthlyTimeBlock> compareMonthlyTimeBlocks;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CompareMonthlyTimeBlock {
        private String timeRange;
        private Map<String, Integer> values;
        private Map<String, String> percentage;
    }
}
