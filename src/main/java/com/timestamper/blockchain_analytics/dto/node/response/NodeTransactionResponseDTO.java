package com.timestamper.blockchain_analytics.dto.node.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NodeTransactionResponseDTO {
    private String txHash;
    private String direction;
    private String toAddress;
    private Double totalValue;

}
