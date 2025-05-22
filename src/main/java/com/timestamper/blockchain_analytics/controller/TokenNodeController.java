package com.timestamper.blockchain_analytics.controller;

import com.timestamper.blockchain_analytics.dto.tokenNode.request.*;
import com.timestamper.blockchain_analytics.dto.tokenNode.response.*;
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

    @PostMapping("dailyVolume")
    public TokenNodeDailyVolumeResponseDTO getDailyTransactionVolume(@RequestBody TokenNodeDailyVolumeRequestDTO requestDTO) {
        return tokenNodeService.getDailyTransactionVolume(requestDTO);
    }

    @PostMapping("monthlyVolume")
    public TokenNodeMonthlyVolumeResponseDTO getMonthlyTransactionVolume(@RequestBody TokenNodeMonthlyVolumeRequestDTO requestDTO) {
        return tokenNodeService.getMonthlyVolume(requestDTO);
    }

    @PostMapping("totalVolume")
    public TokenNodeTotalVolumeResponseDTO getTotalTransactionVolume(@RequestBody TokenNodeTotalVolumeRequestDTO requestDTO) {
        return tokenNodeService.getTotalTransactionVolume(requestDTO);
    }

    @PostMapping("chainInfo")
    public TokenNodeChainInfoResponseDTO getChainInfo(@RequestBody TokenNodeChainInfoRequestDTO requestDTO) {
        return tokenNodeService.getChainInfo(requestDTO);
    }

}
