package com.timestamper.blockchain_analytics.controller;

import com.timestamper.blockchain_analytics.dto.bargraph.reqeust.DailyTransactionRequestDTO;
import com.timestamper.blockchain_analytics.dto.bargraph.reqeust.MonthTransactionRequestDTO;
import com.timestamper.blockchain_analytics.dto.bargraph.response.DailyTransactionResponseDTO;
import com.timestamper.blockchain_analytics.dto.bargraph.response.MonthTransactionResponseDTO;
import com.timestamper.blockchain_analytics.service.BarGraphService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/bargraph")
public class BarGraphController {

    private final BarGraphService barGraphService;

    @PostMapping("monthlyVolume")
    public MonthTransactionResponseDTO getMonthlyTransactionVolume(@RequestBody MonthTransactionRequestDTO request) {
        return barGraphService.getMonthlyTransactionVolume(request);
    }

    @PostMapping("dailyVolume")
    public DailyTransactionResponseDTO getDailyTransactionVolume(@RequestBody DailyTransactionRequestDTO request) {
        return barGraphService.getDailyTransactionVolume(request);
    }
}
