package com.timestamper.blockchain_analytics.dto.heatmap;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DataTransactionDTO {
    private int rank;
    private String token;
    private Long transactionCount;
    private double totalAmount;
    private double currentMarketPrice;
}
