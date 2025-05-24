package com.timestamper.blockchain_analytics.dto.transaction;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class SecondToAddressRawDTO {
    private String token;
    private String ecosystem;
    private BigDecimal value;
    private BigDecimal totalValue;
}
