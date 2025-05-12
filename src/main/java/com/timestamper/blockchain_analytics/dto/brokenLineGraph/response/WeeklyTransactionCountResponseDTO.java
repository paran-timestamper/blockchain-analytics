package com.timestamper.blockchain_analytics.dto.brokenLineGraph.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WeeklyTransactionCountResponseDTO {
    private String day;
    private int txCount;
}
