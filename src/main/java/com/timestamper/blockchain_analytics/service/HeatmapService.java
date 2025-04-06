package com.timestamper.blockchain_analytics.service;

import com.timestamper.blockchain_analytics.dto.heatmap.DataTransactionDTO;
import com.timestamper.blockchain_analytics.dto.heatmap.DateInfoDTO;
import com.timestamper.blockchain_analytics.dto.heatmap.request.DateInfoRequestDTO;
import com.timestamper.blockchain_analytics.dto.heatmap.response.DataInfoResponseDTO;
import com.timestamper.blockchain_analytics.dto.heatmap.response.DataTransactionResponseDTO;
import com.timestamper.blockchain_analytics.repository.HeatmapRepository;
import com.timestamper.blockchain_analytics.service.crypto.CryptoPriceService;
import com.timestamper.blockchain_analytics.repository.projection.HeatmapTransactionProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class HeatmapService {

    private final CryptoPriceService cryptoPriceService;
    private final HeatmapRepository heatmapRepository;

    public DataInfoResponseDTO getInfo(DateInfoRequestDTO request) {
        LocalDateTime startDateTime = request.getStartDate();
        LocalDateTime endDateTime = startDateTime.plusDays(7);

        List<DateInfoDTO> dateInfoList = heatmapRepository.getInfo(startDateTime, endDateTime).stream()
                .map(p -> new DateInfoDTO(p.getDay().trim(), p.getHour(), p.getTxCount()))
                .toList();

        return new DataInfoResponseDTO(dateInfoList);
    }

    public DataTransactionResponseDTO getTransactionInfo(DateInfoRequestDTO request) {
        LocalDateTime startDateTime = request.getStartDate();
        LocalDateTime endDateTime = startDateTime.plusHours(1);
        List<DataTransactionDTO> transactionInfo = getTransactionInfo(startDateTime, endDateTime);

        return new DataTransactionResponseDTO(transactionInfo);
    }

    private List<DataTransactionDTO> getTransactionInfo(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        List<HeatmapTransactionProjection> rawData = heatmapRepository.getTransactionInfo(startDateTime, endDateTime);
        if (rawData.isEmpty()) {
            return Collections.emptyList();
        }
        List<String> symbols = rawData.stream().map(HeatmapTransactionProjection::getToken).toList();
        Map<String, Double> priceMap = cryptoPriceService.getMultipleCryptoPrices(symbols, "USD").block();
        return rawData.stream()
                .map(p -> {
                    assert priceMap != null;
                    double currentPrice = priceMap.getOrDefault(p.getToken(), 0.0);
                    return new DataTransactionDTO(
                            p.getRank(),
                            p.getToken(),
                            p.getTransactionCount(),
                            p.getTotalAmount(),
                            currentPrice
                    );
                }).toList();
    }
}
