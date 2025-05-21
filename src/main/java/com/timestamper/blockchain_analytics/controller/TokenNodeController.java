package com.timestamper.blockchain_analytics.controller;

import com.timestamper.blockchain_analytics.dto.tokenNode.request.TokenNodeDailyVolumeRequestDTO;
import com.timestamper.blockchain_analytics.dto.tokenNode.request.TokenNodeMonthlyVolumeRequestDTO;
import com.timestamper.blockchain_analytics.dto.tokenNode.request.TokenNodeTotalVolumeRequestDTO;
import com.timestamper.blockchain_analytics.dto.tokenNode.request.TokenNodeTransactionRequestDTO;
import com.timestamper.blockchain_analytics.dto.tokenNode.response.TokenNodeGraphResponseDTO;
import com.timestamper.blockchain_analytics.dto.tokenNode.response.TokenNodeMonthlyVolumeResponseDTO;
import com.timestamper.blockchain_analytics.dto.tokenNode.response.TokenNodeTotalVolumeResponseDTO;
import com.timestamper.blockchain_analytics.dto.tokenNode.response.TokenNodeTransactionResponseDTO;
import com.timestamper.blockchain_analytics.service.TokenNodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

    @PostMapping("transaction")
    public TokenNodeTransactionResponseDTO getTransaction(@RequestBody TokenNodeTransactionRequestDTO requestDTO) {
        return tokenNodeService.getTransaction(requestDTO);
    }

    // TODO Main > TokenNode > 두 노드 클릭 > 일간 거래량
//    @PostMapping("dailyVolume")
//    public TokenNodeDailyVolumeResponseDTO getDailyTransactionVolume(@RequestBody TokenNodeDailyVolumeRequestDTO requestDTO) {
//        return tokenNodeService.getDailyTransactionVolume(requestDTO);
//    }
//
    @PostMapping("monthlyVolume")
    public TokenNodeMonthlyVolumeResponseDTO getMonthlyTransactionVolume(@RequestBody TokenNodeMonthlyVolumeRequestDTO requestDTO) {
        return tokenNodeService.getMonthlyVolume(requestDTO);
    }

    @PostMapping("totalVolume")
    public TokenNodeTotalVolumeResponseDTO getTotalTransactionVolume(@RequestBody TokenNodeTotalVolumeRequestDTO requestDTO) {
        return tokenNodeService.getTotalTransactionVolume(requestDTO);
    }

    // TODO Main > TokenNode > 두 노드 클릭 > heatmap


    // TODO Main > TokenNode > 두 노드 클릭 > heatmap > 클릭 시 테이블


    // TODO Main > TokenNode > 두 노드 클릭 > 주로 사용한 체인


}
