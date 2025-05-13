package com.timestamper.blockchain_analytics.service;

import com.timestamper.blockchain_analytics.dto.chart.response.ChartDataResponse;
import com.timestamper.blockchain_analytics.dto.chart.response.TransactionResponseDTO;
import com.timestamper.blockchain_analytics.entity.Transaction;
import com.timestamper.blockchain_analytics.enums.Filter;
import com.timestamper.blockchain_analytics.repository.ChartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChartService {

    private final ChartRepository chartRepository;

    public ChartDataResponse getChartData(String token, Filter filter, String fromAddress, int page, int size) {
        return switch (filter) {
            case LATEST -> getChartDataByLatest(token, fromAddress, page, size);
            case VALUE -> getChartDataByValue(token, fromAddress, page, size);
            default -> throw new IllegalArgumentException("Invalid filter type: " + filter);
        };
    }

    private ChartDataResponse getChartDataByLatest(String token, String fromAddress, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        List<Object[]> rawList = chartRepository.findByLatest(token, fromAddress, pageable);
        int totalCount = chartRepository.countFiltered(token, fromAddress);

        List<TransactionResponseDTO> transactions = convertToDto(rawList);

        return ChartDataResponse.builder()
                .transactions(transactions)
                .filter(Filter.LATEST)
                .page(page)
                .size(size)
                .totalCount(totalCount)
                .build();
    }

    private ChartDataResponse getChartDataByValue(String token, String fromAddress, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        List<Object[]> rawList = chartRepository.findByValue(token, fromAddress, pageable);
        int totalCount = chartRepository.countFiltered(token, fromAddress);

        List<TransactionResponseDTO> transactions = convertToDto(rawList);

        return ChartDataResponse.builder()
                .transactions(transactions)
                .filter(Filter.VALUE)
                .page(page)
                .size(size)
                .totalCount(totalCount)
                .build();
    }

    private List<TransactionResponseDTO> convertToDto(List<Object[]> rawList) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        return rawList.stream().map(obj -> {
            Transaction t = (Transaction) obj[0];
            BigDecimal price = (BigDecimal) obj[1];

            BigDecimal amount = BigDecimal.valueOf(t.getAmount());
            Double value = amount != null ? amount.doubleValue() : 0.0;
            Double priceValue = price != null ? price.doubleValue() : 0.0;
            Double totalPrice = (amount != null && price != null)
                    ? amount.multiply(price).doubleValue()
                    : 0.0;

            return TransactionResponseDTO.builder()
                    .txHash(t.getTxhash())
                    .fromAddress(t.getFromAddress())
                    .toAddress(t.getToAddress())
                    .value(value)
                    .price(priceValue)
                    .totalPrice(totalPrice)
                    .dpDenom(t.getDpdenom())
                    .convertedDate(t.getConvertedDate().format(formatter))
                    .build();
        }).collect(Collectors.toList());
    }
}
