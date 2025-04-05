package com.timestamper.blockchain_analytics.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class TransactionFilteredAmountResponse {
    private Double currentPrice;
    private List<TransactionFilteredAmountDTO> transactions;
}
