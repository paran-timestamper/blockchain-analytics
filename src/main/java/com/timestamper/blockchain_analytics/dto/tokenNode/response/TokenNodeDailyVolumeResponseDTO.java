package com.timestamper.blockchain_analytics.dto.tokenNode.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
public class TokenNodeDailyVolumeResponseDTO {
    private List<String> tokens;
    private List<TimeBlock> time_blocks;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TimeBlock {
        private String time_range;             // 예: "00:00 ~ 03:59"
        private Map<String, Double> values;    // token → 거래량
    }
}
