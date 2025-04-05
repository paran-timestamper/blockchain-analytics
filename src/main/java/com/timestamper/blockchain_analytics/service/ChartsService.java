package com.timestamper.blockchain_analytics.service;

import com.timestamper.blockchain_analytics.dto.charts.TransactionInfoDTO;
import com.timestamper.blockchain_analytics.dto.charts.request.ChartsTransactionRequest;
import com.timestamper.blockchain_analytics.dto.charts.response.ChartsTransactionResponse;
import com.timestamper.blockchain_analytics.repository.ChartsRepository;
import com.timestamper.blockchain_analytics.utils.api.TokenPriceAPIClient;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ChartsService {

    private final static String CURRENCY = "USD";

    private final ChartsRepository chartsRepository;
    private final TokenPriceAPIClient tokenPriceAPIClient;

    public ChartsTransactionResponse getChartsFilteredLatest(ChartsTransactionRequest chartsTransactionRequest) {
        Pageable pageable = getPageable(chartsTransactionRequest);
        double currentTokenPrice = tokenPriceAPIClient.updatePrice(chartsTransactionRequest.getToken(), CURRENCY);
        Page<TransactionInfoDTO> pageResult = getResultByLatest(chartsTransactionRequest, pageable, currentTokenPrice);

        return new ChartsTransactionResponse(
                pageResult.getContent(),
                currentTokenPrice,
                chartsTransactionRequest.getPage(),
                chartsTransactionRequest.getSize(),
                pageResult.getTotalElements()
        );
    }

    private Page<TransactionInfoDTO> getResultByLatest(ChartsTransactionRequest chartsTransactionRequest, Pageable pageable, double currentTokenPrice) {
        Page<TransactionInfoDTO> pageResult;

        // 날짜 필터링이 있는 경우
        if (chartsTransactionRequest.getStartDate() != null && chartsTransactionRequest.getEndDate() != null) {
            LocalDateTime startDate = chartsTransactionRequest.getStartDate().atStartOfDay();
            LocalDateTime endDate = chartsTransactionRequest.getEndDate().plusDays(1).atStartOfDay();

            pageResult = chartsRepository.findFilteredTransactionsByLatestAndDate(
                    chartsTransactionRequest.getToken(),
                    currentTokenPrice,
                    startDate,
                    endDate,
                    pageable
            );
        } else {
            pageResult = chartsRepository.findFilteredTransactionsByLatest(
                    chartsTransactionRequest.getToken(),
                    currentTokenPrice,
                    pageable
            );
        }
        return pageResult;
    }

    public ChartsTransactionResponse getChartsFilteredByPrice(ChartsTransactionRequest chartsTransactionRequest) {
        Pageable pageable = getPageable(chartsTransactionRequest);
        double currentTokenPrice = tokenPriceAPIClient.updatePrice(chartsTransactionRequest.getToken(), CURRENCY);
        Page<TransactionInfoDTO> pageResult = getResultByPrice(chartsTransactionRequest, pageable, currentTokenPrice);

        return new ChartsTransactionResponse(
                pageResult.getContent(),
                currentTokenPrice,
                chartsTransactionRequest.getPage(),
                chartsTransactionRequest.getSize(),
                pageResult.getTotalElements()
        );
    }

    private Page<TransactionInfoDTO> getResultByPrice(ChartsTransactionRequest chartsTransactionRequest, Pageable pageable, double currentTokenPrice) {
        Page<TransactionInfoDTO> pageResult;

        // 날짜 필터링이 있는 경우
        if (chartsTransactionRequest.getStartDate() != null && chartsTransactionRequest.getEndDate() != null) {
            LocalDateTime startDate = chartsTransactionRequest.getStartDate().atStartOfDay();
            LocalDateTime endDate = chartsTransactionRequest.getEndDate().plusDays(1).atStartOfDay();

            pageResult = chartsRepository.findFilteredTransactionsByPriceAndDate(
                    chartsTransactionRequest.getToken(),
                    currentTokenPrice,
                    startDate,
                    endDate,
                    pageable
            );
        } else {
            pageResult = chartsRepository.findFilteredTransactionsByPrice(
                    chartsTransactionRequest.getToken(),
                    currentTokenPrice,
                    pageable
            );
        }
        return pageResult;
    }

    public ChartsTransactionResponse getChartsFilteredByAmount(ChartsTransactionRequest chartsTransactionRequest) {
        Pageable pageable = getPageable(chartsTransactionRequest);
        double currentTokenPrice = tokenPriceAPIClient.updatePrice(chartsTransactionRequest.getToken(), CURRENCY);
        Page<TransactionInfoDTO> pageResult = getResultByTransactionAmount(chartsTransactionRequest, pageable, currentTokenPrice);

        return new ChartsTransactionResponse(
                pageResult.getContent(),
                currentTokenPrice,
                chartsTransactionRequest.getPage(),
                chartsTransactionRequest.getSize(),
                pageResult.getTotalElements()
        );
    }

    private Page<TransactionInfoDTO> getResultByTransactionAmount(ChartsTransactionRequest chartsTransactionRequest, Pageable pageable, double currentTokenPrice) {
        Page<TransactionInfoDTO> pageResult;

        // 날짜 필터링이 있는 경우
        if (chartsTransactionRequest.getStartDate() != null && chartsTransactionRequest.getEndDate() != null) {
            LocalDateTime startDate = chartsTransactionRequest.getStartDate().atStartOfDay();
            LocalDateTime endDate = chartsTransactionRequest.getEndDate().plusDays(1).atStartOfDay();

            pageResult = chartsRepository.findFilteredTransactionsByAmountAndDate(
                    chartsTransactionRequest.getToken(),
                    currentTokenPrice,
                    startDate,
                    endDate,
                    pageable
            );
        } else {
            pageResult = chartsRepository.findFilteredTransactionsByAmount(
                    chartsTransactionRequest.getToken(),
                    currentTokenPrice,
                    pageable
            );
        }
        return pageResult;
    }

    private Pageable getPageable(ChartsTransactionRequest chartsTransactionRequest) {
        return PageRequest.of(
                Math.max(0, chartsTransactionRequest.getPage() - 1),
                chartsTransactionRequest.getSize()
        );
    }
}

