package com.timestamper.blockchain_analytics.service;

import com.timestamper.blockchain_analytics.dto.charts.ChartsFilteredAmountResponse;
import com.timestamper.blockchain_analytics.dto.charts.ChartsFilteredLatestDTO;
import com.timestamper.blockchain_analytics.dto.charts.ChartsFilteredVolumeDTO;
import com.timestamper.blockchain_analytics.entity.Transaction;
import com.timestamper.blockchain_analytics.repository.ChartsRepository;
import com.timestamper.blockchain_analytics.utils.api.TokenPriceAPIClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChartsService {

    private final TokenPriceAPIClient tokenPriceAPIClient;
    private final ChartsRepository chartsRepository;

    public List<ChartsFilteredVolumeDTO> getChartsFilteredVolume(String token) {
        return chartsRepository.findByChartsFilteredVolume(token);
    }

    public List<ChartsFilteredLatestDTO> getChartsFilteredLatest(String token) {
        return chartsRepository.findByChartsFilteredLatest(token);
    }

    public ChartsFilteredAmountResponse getChartsFilteredAmount(String token) {
        return new ChartsFilteredAmountResponse(tokenPriceAPIClient.updatePrice(token, "USD"),
                                                     chartsRepository.findByChartsFilteredAmount(token));
    }

    public List<Transaction> getChartsByAddress(String token, String fromAddress) {
        return chartsRepository.findByDpdenomAndFromAddressOrderByTimestampDesc(token, fromAddress);
    }
}
