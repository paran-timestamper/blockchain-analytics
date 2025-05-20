package com.timestamper.blockchain_analytics.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TransactionFilteredAmountResponse {
    private Double currentPrice;
    private List<TransactionFilteredAmountDTO> transactions;
}
