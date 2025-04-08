package com.timestamper.blockchain_analytics.service;

import com.timestamper.blockchain_analytics.dto.visual.NodeInfoDTO;
import com.timestamper.blockchain_analytics.dto.visual.response.NodeInfosResponseDTO;
import com.timestamper.blockchain_analytics.repository.VisualRepository;
import com.timestamper.blockchain_analytics.repository.projection.visual.NodeInfoProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VisualService {

    private final VisualRepository visualRepository;

    public NodeInfosResponseDTO getNodeInfos(String fromAddress) {
        List<NodeInfoProjection> nodeInfos = visualRepository.getNodeInfos(fromAddress);

        // TODO totalValue 어떻게 계산하는지 물어보고 구현해야함 ㄱㄱ
        double totalValue = 0.0;

        List<NodeInfoDTO> list = nodeInfos.stream()
                .map(nodeInfo -> new NodeInfoDTO(
                        nodeInfo.getTxHash(),
                        nodeInfo.getType(),
                        nodeInfo.getToAddress(),
                        totalValue
                ))
                .toList();

        return new NodeInfosResponseDTO(list);
    }
}
