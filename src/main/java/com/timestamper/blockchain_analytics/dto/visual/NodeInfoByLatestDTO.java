package com.timestamper.blockchain_analytics.dto.visual;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NodeInfoByLatestDTO {
    private String toAddress;
    private boolean isExchange;
}
