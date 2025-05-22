package com.timestamper.blockchain_analytics.controller;

import com.timestamper.blockchain_analytics.dto.heatmap.request.*;
import com.timestamper.blockchain_analytics.dto.heatmap.response.*;
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

    // 두 토큰의 value를 비교하는 API
    @PostMapping("/compare/monthly")
    public CompareMonthlyResponseDTO getCompareMonthlyInfo(@RequestBody CompareMonthlyRequestDTO request) {
        return heatmapService.getCompareMonthlyInfo(request.getTokens(), request.getDate());
    }

//    @PostMapping("/compare/daily")
//    public CompareDailyResponseDTO getCompareDailyInfo(@RequestBody CompareDailyRequestDTO request) {
//        return heatmapService.getCompareDailyInfo();
//    }
}
