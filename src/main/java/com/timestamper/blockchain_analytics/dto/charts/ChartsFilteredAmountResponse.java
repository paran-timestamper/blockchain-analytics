package com.timestamper.blockchain_analytics.dto.charts;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ChartsFilteredAmountResponse {
    private Double currentPrice;
    private List<ChartsFilteredAmountDTO> transactions;
}
