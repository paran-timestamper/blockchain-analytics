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
     * Get all tokens
     * @return
     */
    @GetMapping("tokens")
    public List<String> getTokens() {
        return transactionService.getAllTokens();
    }

    /**
     * Get transactions filtered by volume
     * @param token
     * @return
     */
    @GetMapping("nodeInfos/volume")
    public List<TransactionFilteredVolumeDTO> getTransactionsFilteredByVolume(@RequestParam String token) {
        return transactionService.getTransactionFilteredVolume(token);
    }

    /**
     * Get transactions filtered by latest
     * @param token
     * @return
     */
    @GetMapping("nodeInfos/latest")
    public List<TransactionFilteredLatestDTO> getTransactionsFilteredByLatest(@RequestParam String token) {
        return transactionService.getTransactionFilteredLatest(token);
    }

    /**
     * Get transactions filtered by amount
     * @param token
     * @return
     */
    @GetMapping("nodeInfos/amount")
    public TransactionFilteredAmountResponse getTransactionsFilteredByAmount(@RequestParam String token) {
        return transactionService.getTransactionFilteredAmount(token);
    }

    /**
     * Get transactions filtered by address
     * @param token
     * @param fromAddress
     * @return
     */
    @GetMapping("infos/address")
    public List<Transaction> getTransactionsFilteredByAddress(@RequestParam String token, @RequestParam String fromAddress) {
        return transactionService.getTransactionFilteredByAddress(token, fromAddress);
    }

}
