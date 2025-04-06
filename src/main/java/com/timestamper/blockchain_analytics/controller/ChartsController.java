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
    @GetMapping("infos/latest")
    public ChartsTransactionResponseDTO getChartsFilteredByLatest(@RequestBody ChartsTransactionRequestDTO chartsTransactionRequestDTO) {
        return chartsService.getChartsFilteredLatest(chartsTransactionRequestDTO);
    }

    /**
     * @description Charts > 거래대금 데이터 (paging적용) - 연, 월, 일 필터링
     * @param chartsTransactionRequestDTO
     * @return ChartsTransactionResponse
     */
    @GetMapping("infos/transactionPrice")
    public ChartsTransactionResponseDTO getChartsFilteredByPrice(@RequestBody ChartsTransactionRequestDTO chartsTransactionRequestDTO) {
        return chartsService.getChartsFilteredByPrice(chartsTransactionRequestDTO);
    }

//    TODO transaction 필터링은 출력이 다른방식으로 되어야할거 같다.
//    /**
//     * @description Charts > 트랜잭션량 데이터 (paging적용) - 연, 월, 일 필터링
//     * @param chartsTransactionRequest
//     * @return ChartsTransactionResponse
//     */
//    @PostMapping("infos/transactionAmount")
//    public ChartsTransactionResponse getChartsFilteredByTransactionAmount(@RequestBody ChartsTransactionRequest chartsTransactionRequest) {
//        return chartsService.getChartsFilteredByAmount(chartsTransactionRequest);
//    }

    @GetMapping("infos/transaction")
    public List<Transaction> getTransactionByAddress(@RequestBody AddressTransactionRequestDTO request) {
        return chartsService.getTransactionByAddress(request);
    }

}
