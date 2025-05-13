package com.timestamper.blockchain_analytics.dto.chart.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionResponseDTO {
    private String txHash;
    private String fromAddress;
    private String toAddress;
    private Double value;         // ← Double로 변경
    private Double price;         // ← Double로 변경
    private Double totalPrice;    // ← Double로 변경
    private String dpDenom;
    private String convertedDate;
}
