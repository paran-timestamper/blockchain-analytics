package com.timestamper.blockchain_analytics.dto.visual;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NodeInfoDTO {
    private String txHash;
    private String type;
    private String toAddress;
    private double totalValue;
}
