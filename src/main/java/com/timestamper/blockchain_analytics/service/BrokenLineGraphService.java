package com.timestamper.blockchain_analytics.service;

import com.timestamper.blockchain_analytics.dto.brokenLineGraph.response.WeeklyTransactionCountResponseDTO;
import com.timestamper.blockchain_analytics.dto.brokenLineGraph.response.WeeklyTransactionVolumeResponseDTO;
import com.timestamper.blockchain_analytics.repository.BrokenLineGraphRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BrokenLineGraphService {

    private final BrokenLineGraphRepository brokenLineGraphRepository;

    public List<WeeklyTransactionCountResponseDTO> getWeeklyTransactionCountInfo(String fromAddress, String token, LocalDate date) {
        return brokenLineGraphRepository.selectWeeklyTransactionCount(fromAddress, token, String.valueOf(date)).stream()
                .map(row -> new WeeklyTransactionCountResponseDTO((String) row[0], ((Number) row[1]).intValue())).toList();
    }

    public List<WeeklyTransactionVolumeResponseDTO> getWeeklyTransactionValueInfo(String fromAddress, String token, LocalDate date) {
        return brokenLineGraphRepository.selectWeeklyTransactionVolume(fromAddress, token, String.valueOf(date)).stream()
                .map(row -> new WeeklyTransactionVolumeResponseDTO((String) row[0], ((Number) row[1]).doubleValue())).toList();
    }
}
