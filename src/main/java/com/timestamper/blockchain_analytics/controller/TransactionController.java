package com.timestamper.blockchain_analytics.controller;

import com.timestamper.blockchain_analytics.dto.TransactionFilteredAmountDTO;
import com.timestamper.blockchain_analytics.dto.TransactionFilteredAmountResponse;
import com.timestamper.blockchain_analytics.dto.TransactionFilteredLatestDTO;
import com.timestamper.blockchain_analytics.dto.TransactionFilteredVolumeDTO;
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
     * Get all tokens
     * @return
     */
    @GetMapping("getTokens")
    public List<String> getTokens() {
        return transactionService.getTokens();
    }

    /**
     * Get transactions filtered by volume
     * @param token
     * @return
     */
    @GetMapping("getNodeInfos/volume")
    public List<TransactionFilteredVolumeDTO> getTransactionsFilteredByVolume(@RequestParam String token) {
        return transactionService.getTransactionFilteredVolume(token);
    }

    /**
     * Get transactions filtered by latest
     * @param token
     * @return
     */
    @GetMapping("getNodeInfos/latest")
    public List<TransactionFilteredLatestDTO> getTransactionsFilteredByLatest(@RequestParam String token) {
        return transactionService.getTransactionFilteredLatest(token);
    }

    /**
     * Get transactions filtered by amount
     * @param token
     * @return
     */
    @GetMapping("getNodeInfos/amount")
    public TransactionFilteredAmountResponse getTransactionsFilteredByAmount(@RequestParam String token) {
        return transactionService.getTransactionFilteredAmount(token);
    }

    // TODO 클릭노드의(수신자, 토큰 정보 이용) 최신 transaction 정보를 가져오는 API 추가


}
