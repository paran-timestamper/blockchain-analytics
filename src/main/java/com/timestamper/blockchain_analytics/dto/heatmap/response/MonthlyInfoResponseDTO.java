package com.timestamper.blockchain_analytics.dto.heatmap.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MonthlyInfoResponseDTO {
    private String day;
    private int txCount;
}
