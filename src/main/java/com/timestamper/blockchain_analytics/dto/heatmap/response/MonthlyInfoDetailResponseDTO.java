package com.timestamper.blockchain_analytics.dto.heatmap.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class MonthlyInfoDetailResponseDTO {
    private List<DataTransaction> dataTransactions;

    @Data
    @AllArgsConstructor
    public static class DataTransaction {
        private int rank;
        private String token;
        private int transactionCount;
        private double totalAmount;
        private double currentMarketPrice;
    }
}
