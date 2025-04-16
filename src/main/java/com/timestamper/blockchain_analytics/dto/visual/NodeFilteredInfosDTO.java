package com.timestamper.blockchain_analytics.dto.visual;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NodeFilteredInfosDTO {
    private String toAddress;
    private boolean isExchange;
    private Double totalValue;
    private Integer transactionCount;
    private String convertedDate;
}
