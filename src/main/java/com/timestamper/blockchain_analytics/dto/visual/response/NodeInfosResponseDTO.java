package com.timestamper.blockchain_analytics.dto.visual.response;

import com.timestamper.blockchain_analytics.dto.visual.NodeInfoDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class NodeInfosResponseDTO {
    List<NodeInfoDTO> nodeInfoList;

    public NodeInfosResponseDTO(String txHash, String type, String toAddress, double totalValue) {
    }
}
