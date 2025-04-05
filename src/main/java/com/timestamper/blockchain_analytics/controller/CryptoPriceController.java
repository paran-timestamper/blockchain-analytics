package com.timestamper.blockchain_analytics.controller;

import com.timestamper.blockchain_analytics.service.crypto.CryptoPriceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/crypto")
public class CryptoPriceController {
    private final CryptoPriceService cryptoPriceService;

    /**
     * @param symbol
     * @param currency
     * @return Crypto price in the specified currency
     * @description Get the current price of a cryptocurrency in a specified currency
     */
    @GetMapping("/price")
    public Mono<Double> getCryptoPrice(
            @RequestParam String symbol,
            @RequestParam String currency) {
        return cryptoPriceService.getCryptoPrice(symbol, currency);
    }
}
