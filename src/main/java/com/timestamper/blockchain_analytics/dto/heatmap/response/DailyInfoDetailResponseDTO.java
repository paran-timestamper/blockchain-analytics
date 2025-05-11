package com.timestamper.blockchain_analytics.dto.heatmap.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class DailyInfoDetailResponseDTO {
    private List<DataDailyTransaction> dataDailyTransactions;

    @Data
    @AllArgsConstructor
    public static class DataDailyTransaction {
        private int rank;
        private String token;
        private int transactionCount;
        private double totalAmount;
        private double currentMarketPrice;
    }
}
