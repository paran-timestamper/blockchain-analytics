package com.timestamper.blockchain_analytics.service;

import com.timestamper.blockchain_analytics.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public List<String> getTokens() { return transactionRepository.findDistinctDpdenom(); }

}
