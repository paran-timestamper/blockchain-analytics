package com.timestamper.blockchain_analytics.controller;

import com.timestamper.blockchain_analytics.dto.charts.ChartsFilteredAmountResponse;
import com.timestamper.blockchain_analytics.dto.charts.ChartsFilteredLatestDTO;
import com.timestamper.blockchain_analytics.dto.charts.ChartsFilteredVolumeDTO;
import com.timestamper.blockchain_analytics.entity.Transaction;
import com.timestamper.blockchain_analytics.service.ChartsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/transaction")
public class ChartsController {

    private final ChartsService chartsService;

    /**
     * Get transactions filtered by volume
     * 트랜잭션량
     * @param token
     * @return
     */
    @GetMapping("nodeInfos/volume")
    public List<ChartsFilteredVolumeDTO> getChartsFilteredByVolume(@RequestParam String token) {
        return chartsService.getChartsFilteredVolume(token);
    }

    /**
     * Get transactions filtered by latest
     * 최신순
     * @param token
     * @return
     */
    @GetMapping("nodeInfos/latest")
    public List<ChartsFilteredLatestDTO> getChartsFilteredByLatest(@RequestParam String token) {
        return chartsService.getChartsFilteredLatest(token);
    }

    /**
     * Get transactions filtered by amount
     * 거래 대금
     * @param token
     * @return
     */
    @GetMapping("nodeInfos/amount")
    public ChartsFilteredAmountResponse getChartsFilteredByAmount(@RequestParam String token) {
        return chartsService.getChartsFilteredAmount(token);
    }

    /**
     * Get transactions by address
     * @param token
     * @param fromAddress
     * @return
     */
    @GetMapping("infos/address")
    public List<Transaction> getChartsByAddress(@RequestParam String token, @RequestParam String fromAddress) {
        return chartsService.getChartsByAddress(token, fromAddress);
    }

    // TODO infos/address 필터링 기능


    // TODO infos/address 일, 월 그래프 데이터 필터링 기능


    // TODO 고래 데이터 필터링 기능


    // TODO 히트맵 데이터 필터링 기능
}
