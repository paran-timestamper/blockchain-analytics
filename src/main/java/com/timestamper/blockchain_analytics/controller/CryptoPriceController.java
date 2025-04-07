package com.timestamper.blockchain_analytics.controller;

import com.timestamper.blockchain_analytics.dto.coin.request.CryptoPriceRequestDTO;
import com.timestamper.blockchain_analytics.dto.coin.request.CryptoPricesRequestDTO;
import com.timestamper.blockchain_analytics.service.crypto.CryptoPriceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/crypto")
public class CryptoPriceController {
    private final CryptoPriceService cryptoPriceService;

    /**
     * @description Main > 암호화폐 단일 가격 조회
     * @param request
     * @return
     */
    @GetMapping("/price")
    public Mono<Double> getCryptoPrice(@RequestBody CryptoPriceRequestDTO request) {
        String currency = request.getCurrency() != null ? request.getCurrency() : "USD";
        return cryptoPriceService.getCryptoPrice(request.getSymbols(), currency);
    }

    /**
     * @description Main > 암호화폐 다중 가격 조회
     * @param request
     * @return
     */
    @GetMapping("/prices")
    public Mono<Map<String, Double>> getCryptoPrices(@RequestBody CryptoPricesRequestDTO request) {
        String currency = request.getCurrency() != null ? request.getCurrency() : "USD";
        return cryptoPriceService.getMultipleCryptoPrices(request.getSymbols(), currency);
    }
}
