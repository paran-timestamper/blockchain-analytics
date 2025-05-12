package com.timestamper.blockchain_analytics.dto.bargraph.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class MonthTransactionResponseDTO {
    private List<MonthTransactionItem> list;

    @Data
    @AllArgsConstructor
    public static class MonthTransactionItem {
        private int transactionAmount;
        private String time;
    }
}
