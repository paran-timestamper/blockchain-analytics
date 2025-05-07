package com.timestamper.blockchain_analytics.controller;

import com.timestamper.blockchain_analytics.dto.node.request.NodeInfosRequestDTO;
import com.timestamper.blockchain_analytics.dto.node.request.NodeTransactionRequestDTO;
import com.timestamper.blockchain_analytics.dto.node.response.NodeInfosResponseDTO;
import com.timestamper.blockchain_analytics.dto.node.response.NodeTransactionResponseDTO;
import com.timestamper.blockchain_analytics.dto.node.response.TOP1AddressResponseDTO;
import com.timestamper.blockchain_analytics.service.NodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/node")
public class NodeController {

    private final NodeService nodeService;

    @PostMapping("TOP1Address")
    public TOP1AddressResponseDTO getTOP1Address() {
        return nodeService.getTOP1Address();
    }

    @PostMapping("nodeInfos")
    public NodeInfosResponseDTO getNodeInfos(@RequestBody NodeInfosRequestDTO request) {
        return nodeService.getNodeInfos(request);
    }

    @PostMapping("clickNodeInfos")
    public NodeInfosResponseDTO getNodeClickInfos(@RequestBody NodeInfosRequestDTO request) {
        return nodeService.getNodeClickInfos(request);
    }

    // TODO paging 기능 되도록 구현
    @PostMapping("nodeTransaction")
    public List<NodeTransactionResponseDTO> getNodeTransaction(@RequestBody NodeTransactionRequestDTO request, Pageable pageable) {
        return nodeService.getNodeTransaction(request, pageable);
    }
}
