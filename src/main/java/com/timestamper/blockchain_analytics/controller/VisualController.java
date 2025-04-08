package com.timestamper.blockchain_analytics.controller;

import com.timestamper.blockchain_analytics.dto.visual.reqeust.NodeAddressRequestDTO;
import com.timestamper.blockchain_analytics.dto.visual.response.NodeInfosResponseDTO;
import com.timestamper.blockchain_analytics.service.VisualService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/visual")
public class VisualController {

    private final VisualService visualService;

    @PostMapping("/nodeInfos")
    public NodeInfosResponseDTO getNodeInfos(@RequestBody NodeAddressRequestDTO request) {
        return visualService.getNodeInfos(request.getFromAddress());
    }
}
