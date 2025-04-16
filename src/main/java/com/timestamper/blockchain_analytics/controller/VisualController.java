package com.timestamper.blockchain_analytics.controller;

import com.timestamper.blockchain_analytics.dto.visual.reqeust.*;
import com.timestamper.blockchain_analytics.dto.visual.response.*;
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
        return visualService.getNodeInfos(request);
    }

    @PostMapping("/nodeInfo/dayTransaction")
    public NodeDayTransactionResponseDTO getNodeDayTransaction(@RequestBody NodeDayTransactionRequestDTO request) {
        return visualService.getNodeDayTransaction(request.getFromAddress(), request.getDate());
    }

    @PostMapping("/nodeInfo/monthTransaction")
    public NodeMonthTransactionResponseDTO getNodeMonthTransaction(@RequestBody NodeMonthTransactionRequestDTO request) {
        return visualService.getNodeMonthTransaction(request.getFromAddress(), request.getDate());
    }

    @PostMapping("/nodeInfos/latest")
    public NodeDpdenomFilteredInfosResponseDTO getNodeInfosByLatest(@RequestBody NodeFilteredInfosRequestDTO request) {
        return visualService.getNodeInfosByLatest(request.getToken(), request.getFromAddress());
    }

    @PostMapping("/nodeInfos/transactionPrice")
    public NodeFilteredInfosResponseDTO getNodeInfosByTransactionPrice(@RequestBody NodeFilteredInfosRequestDTO request) {
        return visualService.getNodeInfosByTransactionPrice(request.getToken(), request.getFromAddress());
    }

    @PostMapping("/nodeInfos/transactionAmount")
    public NodeFilteredInfosResponseDTO getNodeInfosByTransactionAmount(@RequestBody NodeFilteredInfosRequestDTO request) {
        return visualService.getNodeInfosByTransactionAmount(request.getToken(), request.getFromAddress());
    }

    @PostMapping("nodeInfo/price")
    public double getNodeInfoPrice(@RequestBody NodePriceRequestDTO request) {
        return visualService.getNodeInfoPrice(request.getToken(), request.getAmount());
    }
}
