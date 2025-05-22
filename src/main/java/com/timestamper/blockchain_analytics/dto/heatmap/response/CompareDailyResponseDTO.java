package com.timestamper.blockchain_analytics.dto.heatmap.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
public class CompareDailyResponseDTO {

    private LocalDate clickedDate;
    private List<String> tokens;
    private List<TokenDetail> details;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TokenDetail {
        private String token;
        private int transactionAmount;
        private double totalValue;
        private double currentPrice;
    }
}
