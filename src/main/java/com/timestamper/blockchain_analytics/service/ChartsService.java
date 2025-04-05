package com.timestamper.blockchain_analytics.service;

import com.timestamper.blockchain_analytics.dto.charts.TransactionInfoDTO;
import com.timestamper.blockchain_analytics.dto.charts.request.ChartsTransactionRequest;
import com.timestamper.blockchain_analytics.dto.charts.response.ChartsTransactionResponse;
import com.timestamper.blockchain_analytics.repository.ChartsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ChartsService {

    private final ChartsRepository chartsRepository;

    public ChartsTransactionResponse getChartsFilteredLatest(ChartsTransactionRequest chartsTransactionRequest) {
        Pageable pageable = PageRequest.of(
                Math.max(0, chartsTransactionRequest.getPage() - 1),
                chartsTransactionRequest.getSize()
        );

        Page<TransactionInfoDTO> pageResult = getResult(chartsTransactionRequest, pageable);

        return new ChartsTransactionResponse(
                pageResult.getContent(),
                chartsTransactionRequest.getPage(),
                chartsTransactionRequest.getSize(),
                pageResult.getTotalElements()
        );
    }

    private Page<TransactionInfoDTO> getResult(ChartsTransactionRequest chartsTransactionRequest, Pageable pageable) {
        Page<TransactionInfoDTO> pageResult;

        // 날짜 필터링이 있는 경우
        if (chartsTransactionRequest.getStartDate() != null && chartsTransactionRequest.getEndDate() != null) {
            LocalDateTime startDate = chartsTransactionRequest.getStartDate().atStartOfDay();
            LocalDateTime endDate = chartsTransactionRequest.getEndDate().plusDays(1).atStartOfDay();

            pageResult = chartsRepository.findFilteredTransactionsByLatestAndDate(
                    chartsTransactionRequest.getToken(),
                    startDate,
                    endDate,
                    pageable
            );
        } else {
            pageResult = chartsRepository.findFilteredTransactionsByLatest(
                    chartsTransactionRequest.getToken(),
                    pageable
            );
        }
        return pageResult;
    }
}
