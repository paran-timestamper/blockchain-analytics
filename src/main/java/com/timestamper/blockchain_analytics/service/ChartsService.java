package com.timestamper.blockchain_analytics.service;

import com.timestamper.blockchain_analytics.dto.charts.TransactionInfoDTO;
import com.timestamper.blockchain_analytics.dto.charts.request.AddressTransactionRequestDTO;
import com.timestamper.blockchain_analytics.dto.charts.request.ChartsTransactionRequestDTO;
import com.timestamper.blockchain_analytics.dto.charts.response.ChartsTransactionResponseDTO;
import com.timestamper.blockchain_analytics.entity.Transaction;
import com.timestamper.blockchain_analytics.repository.ChartsRepository;
import com.timestamper.blockchain_analytics.utils.api.TokenPriceAPIClient;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChartsService {

    private final static String CURRENCY = "USD";

    private final ChartsRepository chartsRepository;
    private final TokenPriceAPIClient tokenPriceAPIClient;

    public ChartsTransactionResponseDTO getChartsFilteredLatest(ChartsTransactionRequestDTO chartsTransactionRequestDTO) {
        Pageable pageable = getPageable(chartsTransactionRequestDTO);
        double currentTokenPrice = tokenPriceAPIClient.updatePrice(chartsTransactionRequestDTO.getToken(), CURRENCY);
        Page<TransactionInfoDTO> pageResult = getResultByLatest(chartsTransactionRequestDTO, pageable, currentTokenPrice);

        return new ChartsTransactionResponseDTO(
                pageResult.getContent(),
                currentTokenPrice,
                chartsTransactionRequestDTO.getPage(),
                chartsTransactionRequestDTO.getSize(),
                pageResult.getTotalElements()
        );
    }

    private Page<TransactionInfoDTO> getResultByLatest(ChartsTransactionRequestDTO chartsTransactionRequestDTO, Pageable pageable, double currentTokenPrice) {
        Page<TransactionInfoDTO> pageResult;

        // 날짜 필터링이 있는 경우
        if (chartsTransactionRequestDTO.getStartDate() != null && chartsTransactionRequestDTO.getEndDate() != null) {
            LocalDateTime startDate = chartsTransactionRequestDTO.getStartDate().atStartOfDay();
            LocalDateTime endDate = chartsTransactionRequestDTO.getEndDate().plusDays(1).atStartOfDay();

            pageResult = chartsRepository.findFilteredTransactionsByLatestAndDate(
                    chartsTransactionRequestDTO.getToken(),
                    currentTokenPrice,
                    startDate,
                    endDate,
                    pageable
            );
        } else {
            pageResult = chartsRepository.findFilteredTransactionsByLatest(
                    chartsTransactionRequestDTO.getToken(),
                    currentTokenPrice,
                    pageable
            );
        }
        return pageResult;
    }

    public ChartsTransactionResponseDTO getChartsFilteredByPrice(ChartsTransactionRequestDTO chartsTransactionRequestDTO) {
        Pageable pageable = getPageable(chartsTransactionRequestDTO);
        double currentTokenPrice = tokenPriceAPIClient.updatePrice(chartsTransactionRequestDTO.getToken(), CURRENCY);
        Page<TransactionInfoDTO> pageResult = getResultByPrice(chartsTransactionRequestDTO, pageable, currentTokenPrice);

        return new ChartsTransactionResponseDTO(
                pageResult.getContent(),
                currentTokenPrice,
                chartsTransactionRequestDTO.getPage(),
                chartsTransactionRequestDTO.getSize(),
                pageResult.getTotalElements()
        );
    }

    private Page<TransactionInfoDTO> getResultByPrice(ChartsTransactionRequestDTO chartsTransactionRequestDTO, Pageable pageable, double currentTokenPrice) {
        Page<TransactionInfoDTO> pageResult;

        // 날짜 필터링이 있는 경우
        if (chartsTransactionRequestDTO.getStartDate() != null && chartsTransactionRequestDTO.getEndDate() != null) {
            LocalDateTime startDate = chartsTransactionRequestDTO.getStartDate().atStartOfDay();
            LocalDateTime endDate = chartsTransactionRequestDTO.getEndDate().plusDays(1).atStartOfDay();

            pageResult = chartsRepository.findFilteredTransactionsByPriceAndDate(
                    chartsTransactionRequestDTO.getToken(),
                    currentTokenPrice,
                    startDate,
                    endDate,
                    pageable
            );
        } else {
            pageResult = chartsRepository.findFilteredTransactionsByPrice(
                    chartsTransactionRequestDTO.getToken(),
                    currentTokenPrice,
                    pageable
            );
        }
        return pageResult;
    }

    private Pageable getPageable(ChartsTransactionRequestDTO chartsTransactionRequestDTO) {
        return PageRequest.of(
                Math.max(0, chartsTransactionRequestDTO.getPage() - 1),
                chartsTransactionRequestDTO.getSize()
        );
    }

    public List<Transaction> getTransactionByAddress(AddressTransactionRequestDTO request) {
        return chartsRepository.findByFromAddressOrderByTimestampDesc(request.getFromAddress());
    }
}

