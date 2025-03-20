package com.timestamper.blockchain_analytics.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Optional;

@Service
public class CryptoPriceService {
    private final WebClient webClient;

    @Value("${coinmarketcap.api.key}")
    private String apiKey;

    private static final String BASE_URL = "https://pro-api.coinmarketcap.com/v1/cryptocurrency/quotes/latest";

    public CryptoPriceService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl(BASE_URL).build();
    }

    public Mono<Double> getCryptoPrice(String symbol, String currency) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("symbol", symbol)
                        .queryParam("convert", currency)
                        .build())
                .header("X-CMC_PRO_API_KEY", apiKey)
                .retrieve()
                .bodyToMono(Map.class)
                .map(response -> {
                    Map<String, Object> data = (Map<String, Object>) response.get("data");
                    Map<String, Object> cryptoData = (Map<String, Object>) data.get(symbol);
                    Map<String, Object> quote = (Map<String, Object>) cryptoData.get("quote");
                    Map<String, Object> currencyData = (Map<String, Object>) quote.get(currency);
                    return (Double) currencyData.get("price");
                });
    }
}
