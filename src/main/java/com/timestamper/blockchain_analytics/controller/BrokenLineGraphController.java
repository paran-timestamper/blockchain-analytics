package com.timestamper.blockchain_analytics.controller;

import com.timestamper.blockchain_analytics.dto.brokenLineGraph.request.InfoTransactionCountRequestDTO;
import com.timestamper.blockchain_analytics.dto.brokenLineGraph.request.InfoTransactionVolumeRequestDTO;
import com.timestamper.blockchain_analytics.dto.brokenLineGraph.response.WeeklyTransactionCountResponseDTO;
import com.timestamper.blockchain_analytics.dto.brokenLineGraph.response.WeeklyTransactionVolumeResponseDTO;
import com.timestamper.blockchain_analytics.service.BrokenLineGraphService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/brokenLineGraph")
public class BrokenLineGraphController {

    private final BrokenLineGraphService brokenLineGraphService;

    // 트랜잭션기준
    @PostMapping("weeklyTransactionCount")
    public List<WeeklyTransactionCountResponseDTO> getWeeklyTransactionCountInfo(@RequestBody InfoTransactionCountRequestDTO request) {
        return brokenLineGraphService.getWeeklyTransactionCountInfo(request.getFromAddress(), request.getToken(), request.getDate());
    }

    // 거래 대금기준
    @PostMapping("weeklyTransactionVolume")
    public List<WeeklyTransactionVolumeResponseDTO> getWeeklyTransactionValueInfo(@RequestBody InfoTransactionVolumeRequestDTO request) {
        return brokenLineGraphService.getWeeklyTransactionValueInfo(request.getFromAddress(), request.getToken(), request.getDate());
    }

}
