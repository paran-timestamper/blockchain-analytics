package com.timestamper.blockchain_analytics.controller;

import com.timestamper.blockchain_analytics.dto.TransactionFilteredAmountResponse;
import com.timestamper.blockchain_analytics.dto.TransactionFilteredLatestDTO;
import com.timestamper.blockchain_analytics.dto.TransactionFilteredVolumeDTO;
import com.timestamper.blockchain_analytics.entity.Transaction;
import com.timestamper.blockchain_analytics.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/transaction")
public class TransactionController {

    private final TransactionService transactionService;

    /**
     * Get transactions filtered by volume
     * 트랜잭션량
     * @param token
     * @return
     */
    @GetMapping("nodeInfos/volume")
    public List<TransactionFilteredVolumeDTO> getTransactionsFilteredByVolume(@RequestParam String token) {
        return transactionService.getTransactionFilteredVolume(token);
    }

    /**
     * Get transactions filtered by latest
     * 최신순
     * @param token
     * @return
     */
    @GetMapping("nodeInfos/latest")
    public List<TransactionFilteredLatestDTO> getTransactionsFilteredByLatest(@RequestParam String token) {
        return transactionService.getTransactionFilteredLatest(token);
    }

    /**
     * Get transactions filtered by amount
     * 거래 대금
     * @param token
     * @return
     */
    @GetMapping("nodeInfos/amount")
    public TransactionFilteredAmountResponse getTransactionsFilteredByAmount(@RequestParam String token) {
        return transactionService.getTransactionFilteredAmount(token);
    }

    /**
     * Get transactions by address
     * @param token
     * @param fromAddress
     * @return
     */
    @GetMapping("infos/address")
    public List<Transaction> getTransactionsByAddress(@RequestParam String token, @RequestParam String fromAddress) {
        return transactionService.getTransactionByAddress(token, fromAddress);
    }

    // TODO infos/address 필터링 기능


    // TODO infos/address 일, 월 그래프 데이터 필터링 기능


    // TODO 고래 데이터 필터링 기능


    // TODO 히트맵 데이터 필터링 기능
}
