package com.timestamper.blockchain_analytics.controller;

import com.timestamper.blockchain_analytics.dto.charts.request.AddressTransactionRequestDTO;
import com.timestamper.blockchain_analytics.dto.charts.request.ChartsTransactionRequestDTO;
import com.timestamper.blockchain_analytics.dto.charts.response.ChartsTransactionResponseDTO;
import com.timestamper.blockchain_analytics.entity.Transaction;
import com.timestamper.blockchain_analytics.service.ChartsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/charts")
public class ChartsController {

    private final ChartsService chartsService;

    /**
     * @description Charts > 최신순 데이터 (paging적용) - 연, 월, 일 필터링
     * @param chartsTransactionRequestDTO
     * @return ChartsTransactionResponse
     */
    @PostMapping("infos/latest")
    public ChartsTransactionResponseDTO getChartsFilteredByLatest(@RequestBody ChartsTransactionRequestDTO chartsTransactionRequestDTO) {
        return chartsService.getChartsFilteredLatest(chartsTransactionRequestDTO);
    }

    /**
     * @description Charts > 거래대금 데이터 (paging적용) - 연, 월, 일 필터링
     * @param chartsTransactionRequestDTO
     * @return ChartsTransactionResponse
     */
    @PostMapping("infos/transactionPrice")
    public ChartsTransactionResponseDTO getChartsFilteredByPrice(@RequestBody ChartsTransactionRequestDTO chartsTransactionRequestDTO) {
        return chartsService.getChartsFilteredByPrice(chartsTransactionRequestDTO);
    }

    @PostMapping("infos/transaction")
    public List<Transaction> getTransactionByAddress(@RequestBody AddressTransactionRequestDTO request) {
        return chartsService.getTransactionByAddress(request);
    }

    // TODO Main > Chart > Chart 데이터 불러오기 (필터링 : 최신순, 거래대금 2가지 경우만 고려, Address 입력도 고려)
    public void getChartsData() {

    }

}
