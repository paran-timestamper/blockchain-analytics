package com.timestamper.blockchain_analytics.controller;

import com.timestamper.blockchain_analytics.dto.heatmap.request.DateInfoRequestDTO;
import com.timestamper.blockchain_analytics.dto.heatmap.response.DataInfoResponseDTO;
import com.timestamper.blockchain_analytics.dto.heatmap.response.DataTransactionResponseDTO;
import com.timestamper.blockchain_analytics.service.HeatmapService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/heatmap")
public class HeatmapController {

    private final HeatmapService heatmapService;

    @GetMapping("/infos")
    public DataInfoResponseDTO getInfo(@RequestBody DateInfoRequestDTO request) {
        return heatmapService.getInfo(request);
    }

    @GetMapping("/infos/transaction")
    public DataTransactionResponseDTO getTransactionInfo(@RequestBody DateInfoRequestDTO request) {
        return heatmapService.getTransactionInfo(request);
    }

}
