package com.timestamper.blockchain_analytics.dto.node.request;

import lombok.Data;

@Data
public class NodeTransactionRequestDTO {
    private int page;
    private int size;
    private String fromAddress;
    private String token;
}
