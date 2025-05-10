package com.timestamper.blockchain_analytics.controller;

import com.timestamper.blockchain_analytics.dto.heatmap.request.DateInfoRequestDTO;
import com.timestamper.blockchain_analytics.dto.heatmap.response.DataInfoResponseDTO;
import com.timestamper.blockchain_analytics.dto.heatmap.response.DataTransactionResponseDTO;
import com.timestamper.blockchain_analytics.service.HeatmapService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/heatmap")

public class HeatmapController {

    private final HeatmapService heatmapService;

    @PostMapping("/infos")
    public DataInfoResponseDTO getInfo(@RequestBody DateInfoRequestDTO request) {
        return heatmapService.getInfo(request);
    }

    @PostMapping("/infos/transaction")
    public DataTransactionResponseDTO getTransactionInfo(@RequestBody DateInfoRequestDTO request) {
        return heatmapService.getTransactionInfo(request);
    }

    // TODO Main > Node > 우클릭 > Heatmap > 월 거래량
    public void getMonthlyTransactionVolume() {

    }

    // TODO Main > Node > 우클릭 > Heatmap > 일 거래량
    public void getDailyTransactionVolume() {

    }

    // TODO Main > Node > 우클릭 > Heatmap > 월 거래량 > 클릭 시 rank/token/tx 수/총 value/토큰 현재가
    public void getMonthlyTransactionVolumeDetail() {

    }

    // TODO Main > Node > 우클릭 > Heatmap > 일 거래량 > 클릭 시 rank/token/tx 수/총 value/토큰 현재가
    public void getDailyTransactionVolumeDetail() {

    }

}
