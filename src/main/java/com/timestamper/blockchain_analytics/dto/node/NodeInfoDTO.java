package com.timestamper.blockchain_analytics.dto.node;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NodeInfoDTO {
    private String toAddresses;
    private String dpdenom;
    private String riskLevel;

    public NodeInfoDTO(String toAddresses, String dpdenom) {
        this.toAddresses = toAddresses;
        this.dpdenom = dpdenom;
    }
}
