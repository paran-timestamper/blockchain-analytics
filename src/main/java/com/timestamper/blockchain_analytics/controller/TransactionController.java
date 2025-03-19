package com.timestamper.blockchain_analytics.controller;

import com.timestamper.blockchain_analytics.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping("getTokens")
    public List<String> getTokens() {
        return transactionService.getTokens();
    }




}
