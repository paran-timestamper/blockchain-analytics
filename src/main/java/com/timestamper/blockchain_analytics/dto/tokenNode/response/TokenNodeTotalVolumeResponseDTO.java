package com.timestamper.blockchain_analytics.dto.tokenNode.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
public class TokenNodeTotalVolumeResponseDTO {
    private List<String> tokens;
    private List<TimeBlock> time_blocks;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TimeBlock {
        private String time_range;
        private Map<String, Double> values;
    }
}
