package com.timestamper.blockchain_analytics.controller;

import com.timestamper.blockchain_analytics.dto.visual.reqeust.NodeAddressRequestDTO;
import com.timestamper.blockchain_analytics.dto.visual.reqeust.NodeDayTransactionRequestDTO;
import com.timestamper.blockchain_analytics.dto.visual.reqeust.NodeFilteredInfosRequestDTO;
import com.timestamper.blockchain_analytics.dto.visual.reqeust.NodeMonthTransactionRequestDTO;
import com.timestamper.blockchain_analytics.dto.visual.response.NodeDayTransactionResponseDTO;
import com.timestamper.blockchain_analytics.dto.visual.response.NodeFilteredInfosResponseDTO;
import com.timestamper.blockchain_analytics.dto.visual.response.NodeInfosResponseDTO;
import com.timestamper.blockchain_analytics.dto.visual.response.NodeMonthTransactionResponseDTO;
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

    @PostMapping("/nodeInfo/dayTransaction")
    public NodeDayTransactionResponseDTO getNodeDayTransaction(@RequestBody NodeDayTransactionRequestDTO request) {
        return visualService.getNodeDayTransaction(request.getFromAddress(), request.getDate());
    }

    @PostMapping("/nodeInfo/monthTransaction")
    public NodeMonthTransactionResponseDTO getNodeMonthTransaction(@RequestBody NodeMonthTransactionRequestDTO request) {
        return visualService.getNodeMonthTransaction(request.getFromAddress(), request.getDate());
    }

    @PostMapping("/nodeInfos/latest")
    public NodeFilteredInfosResponseDTO getNodeInfosByLatest(@RequestBody NodeFilteredInfosRequestDTO request) {
        return visualService.getNodeInfosByLatest(request.getToken(), request.getFromAddress());
    }

    @PostMapping("/nodeInfos/transactionPrice")
    public NodeFilteredInfosResponseDTO getNodeInfosByTransactionPrice(@RequestBody NodeFilteredInfosRequestDTO request) {
        return visualService.getNodeInfosByTransactionPrice(request.getToken(), request.getFromAddress());
    }


}
