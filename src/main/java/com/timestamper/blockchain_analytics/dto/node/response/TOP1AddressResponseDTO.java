package com.timestamper.blockchain_analytics.dto.node.response;

import com.timestamper.blockchain_analytics.dto.node.NodeInfoDTO;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class TOP1AddressResponseDTO {
    private String fromAddress;
    private List<NodeInfoDTO> toAddresses;
}
