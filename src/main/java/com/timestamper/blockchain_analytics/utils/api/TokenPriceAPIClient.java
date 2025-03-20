package com.timestamper.blockchain_analytics.utils.api;

import com.timestamper.blockchain_analytics.service.CryptoPriceService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class TokenPriceAPIClient {

    private final CryptoPriceService cryptoPriceService;

    @Getter
    private static double price;

    public double updatePrice(String symbol, String currency) {
        String token = symbol.split("\\.")[0];
        log.warn("token = {} ", token);

        price = cryptoPriceService.getCryptoPrice(token, currency).block();
        log.info("Price of {} in {} is {}, symbol is {}", token, currency, price, symbol);

        return price;
    }
}
