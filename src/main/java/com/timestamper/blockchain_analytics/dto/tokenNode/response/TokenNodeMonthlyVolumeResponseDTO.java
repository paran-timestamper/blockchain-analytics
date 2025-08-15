package com.timestamper.blockchain_analytics.dto.tokenNode.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
public class TokenNodeMonthlyVolumeResponseDTO {
    private List<String> tokens;
    private List<MonthlyTimeBlock> time_blocks;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MonthlyTimeBlock {
        private String time_range;
        private Map<String, Double> values;
    }
}
