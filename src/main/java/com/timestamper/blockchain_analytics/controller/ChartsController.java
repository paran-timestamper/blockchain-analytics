package com.timestamper.blockchain_analytics.controller;

import com.timestamper.blockchain_analytics.dto.charts.request.ChartsTransactionRequest;
import com.timestamper.blockchain_analytics.dto.charts.response.ChartsTransactionResponse;
import com.timestamper.blockchain_analytics.service.ChartsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/charts")
public class ChartsController {

    private final ChartsService chartsService;

    /**
     * @description Charts > 최신순 데이터 (paging적용) - 연, 월, 일 필터링
     * @param chartsTransactionRequest
     * @return ChartsTransactionResponse
     */
    @PostMapping("infos/latest")
    public ChartsTransactionResponse getChartsFilteredByLatest(@RequestBody ChartsTransactionRequest chartsTransactionRequest) {
        return chartsService.getChartsFilteredLatest(chartsTransactionRequest);
    }

}
