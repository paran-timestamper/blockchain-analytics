package com.timestamper.blockchain_analytics.controller;

import com.timestamper.blockchain_analytics.dto.tokenNode.response.TokenNodeGraphResponseDTO;
import com.timestamper.blockchain_analytics.service.TokenNodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tokenNode")
public class TokenNodeController {

    private final TokenNodeService tokenNodeService;

    @PostMapping("graph")
    public TokenNodeGraphResponseDTO getGraph() {
        return tokenNodeService.getTokenNodeGraphData();
    }
}
