package com.timestamper.blockchain_analytics.dto.bargraph.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class DailyTransactionResponseDTO {
    private List<DailyTransactionItem> list;

    @Data
    @AllArgsConstructor
    public static class DailyTransactionItem {
        private int transactionAmount;
        private String time;
    }
}
