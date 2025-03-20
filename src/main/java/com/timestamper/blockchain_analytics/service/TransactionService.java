package com.timestamper.blockchain_analytics.service;

import com.timestamper.blockchain_analytics.dto.TransactionFilteredAmountDTO;
import com.timestamper.blockchain_analytics.dto.TransactionFilteredAmountResponse;
import com.timestamper.blockchain_analytics.dto.TransactionFilteredLatestDTO;
import com.timestamper.blockchain_analytics.dto.TransactionFilteredVolumeDTO;
import com.timestamper.blockchain_analytics.repository.TransactionRepository;
import com.timestamper.blockchain_analytics.utils.api.TokenPriceAPIClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TokenPriceAPIClient tokenPriceAPIClient;
    private final TransactionRepository transactionRepository;

    public List<String> getTokens() { return transactionRepository.findDistinctDpdenom(); }

    public List<TransactionFilteredVolumeDTO> getTransactionFilteredVolume(String token) {
        return transactionRepository.findByTransactionFilteredVolume(token);
    }

    public List<TransactionFilteredLatestDTO> getTransactionFilteredLatest(String token) {
        return transactionRepository.findByTransactionFilteredLatest(token);
    }

    public TransactionFilteredAmountResponse getTransactionFilteredAmount(String token) {
        return new TransactionFilteredAmountResponse(tokenPriceAPIClient.updatePrice(token, "USD"),
                                                     transactionRepository.findByTransactionFilteredAmount(token));
    }
}
