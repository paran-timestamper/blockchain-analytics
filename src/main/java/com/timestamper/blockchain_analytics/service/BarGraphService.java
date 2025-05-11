package com.timestamper.blockchain_analytics.service;

import com.timestamper.blockchain_analytics.dto.bargraph.reqeust.DailyTransactionRequestDTO;
import com.timestamper.blockchain_analytics.dto.bargraph.reqeust.MonthTransactionRequestDTO;
import com.timestamper.blockchain_analytics.dto.bargraph.response.DailyTransactionResponseDTO;
import com.timestamper.blockchain_analytics.dto.bargraph.response.MonthTransactionResponseDTO;
import com.timestamper.blockchain_analytics.repository.BarGraphRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.timestamper.blockchain_analytics.dto.bargraph.response.DailyTransactionResponseDTO.*;
import static com.timestamper.blockchain_analytics.dto.bargraph.response.MonthTransactionResponseDTO.*;

@Service
@RequiredArgsConstructor
public class BarGraphService {

    private final BarGraphRepository barGraphRepository;

    public MonthTransactionResponseDTO getMonthlyTransactionVolume(MonthTransactionRequestDTO request) {
        List<Object[]> rawData = barGraphRepository.selectMonthlyTransactionVolume(request.getFromAddress(), request.getDate().toString());
        List<MonthTransactionItem> result = rawData.stream()
                .map(row -> {
                    int transactionAmount = ((Number) row[0]).intValue();
                    String time = (String) row[1];

                    return new MonthTransactionItem(transactionAmount, time);
                })
                .collect(Collectors.toList());

        return new MonthTransactionResponseDTO(result);
    }

    public DailyTransactionResponseDTO getDailyTransactionVolume(DailyTransactionRequestDTO request) {
        List<Object[]> rawData = barGraphRepository.selectDailyTransactionVolume(request.getFromAddress(), request.getDate().toString());
        List<DailyTransactionItem> result = rawData.stream()
                .map(row -> {
                    int transactionAmount = ((Number) row[0]).intValue();
                    String time = (String) row[1];

                    return new DailyTransactionItem(transactionAmount, time);
                })
                .collect(Collectors.toList());

        return new DailyTransactionResponseDTO(result);
    }
}
