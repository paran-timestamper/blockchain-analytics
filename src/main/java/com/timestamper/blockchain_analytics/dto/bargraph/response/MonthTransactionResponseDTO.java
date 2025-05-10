package com.timestamper.blockchain_analytics.dto.bargraph.response;

import lombok.Data;

import java.util.List;

@Data
public class MonthTransactionResponseDTO {
    private List<TimeSlotTransaction> list;

    @Data
    public static class TimeSlotTransaction {
        private int transactionAmount;
        private String time;
    }
}
