package com.timestamper.blockchain_analytics.controller;

import com.timestamper.blockchain_analytics.dto.heatmap.request.DailyInfoDetailRequestDTO;
import com.timestamper.blockchain_analytics.dto.heatmap.request.DailyInfoRequestDTO;
import com.timestamper.blockchain_analytics.dto.heatmap.request.MonthlyInfoDetailRequestDTO;
import com.timestamper.blockchain_analytics.dto.heatmap.request.MonthlyInfoRequestDTO;
import com.timestamper.blockchain_analytics.dto.heatmap.response.DailyInfoDetailResponseDTO;
import com.timestamper.blockchain_analytics.dto.heatmap.response.DailyInfoResponseDTO;
import com.timestamper.blockchain_analytics.dto.heatmap.response.MonthlyInfoDetailResponseDTO;
import com.timestamper.blockchain_analytics.dto.heatmap.response.MonthlyInfoResponseDTO;
import com.timestamper.blockchain_analytics.service.HeatmapService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/heatmap")
public class HeatmapController {

    private final HeatmapService heatmapService;

    @PostMapping("/monthly")
    public List<MonthlyInfoResponseDTO> getMonthlyInfo(@RequestBody MonthlyInfoRequestDTO request) {
        return heatmapService.getMonthlyInfo(request.getFromAddress(), request.getDate());
    }

    @PostMapping("/daily")
    public DailyInfoResponseDTO getDailyInfo(@RequestBody DailyInfoRequestDTO request) {
        return heatmapService.getDailyInfo(request.getFromAddress(), request.getDate());
    }

    @PostMapping("/monthly/detail")
    public MonthlyInfoDetailResponseDTO getMonthlyInfoDetail(@RequestBody MonthlyInfoDetailRequestDTO request) {
        return heatmapService.getMonthlyInfoDetail(request.getFromAddress(), request.getDate());
    }

    @PostMapping("/daily/detail")
    public DailyInfoDetailResponseDTO getDailyInfoDetail(@RequestBody DailyInfoDetailRequestDTO request) {
        return heatmapService.getDailyInfoDetail(request.getFromAddress(), request.getDate());
    }
}
