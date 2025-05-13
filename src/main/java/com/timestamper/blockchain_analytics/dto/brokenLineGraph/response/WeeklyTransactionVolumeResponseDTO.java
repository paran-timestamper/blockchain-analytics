package com.timestamper.blockchain_analytics.dto.brokenLineGraph.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WeeklyTransactionVolumeResponseDTO {
    private String day;
    private Double totalValue;
}
