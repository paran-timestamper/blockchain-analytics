package com.timestamper.blockchain_analytics.controller;

import com.timestamper.blockchain_analytics.dto.chart.request.ChartDataRequest;
import com.timestamper.blockchain_analytics.dto.chart.response.ChartDataResponse;
import com.timestamper.blockchain_analytics.service.ChartService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chart")
public class ChartController {

    private final ChartService chartService;

    @PostMapping("data")
    public ChartDataResponse getChartData(@RequestBody ChartDataRequest request) {
        return chartService.getChartData(request.getToken(), request.getFilter(), request.getFromAddress(), request.getPage(), request.getSize());
    }

}
