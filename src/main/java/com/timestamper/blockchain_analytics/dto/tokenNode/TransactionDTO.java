package com.timestamper.blockchain_analytics.dto.tokenNode;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TransactionDTO {
    private String txHash;
    private String fromAddress;
    private String toAddress;
    private Double value;
}
