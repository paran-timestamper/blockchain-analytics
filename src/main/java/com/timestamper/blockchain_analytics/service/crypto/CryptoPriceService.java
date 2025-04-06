package com.timestamper.blockchain_analytics.service.crypto;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        return fetchCryptoData(List.of(symbol), currency)
                .map(data -> {
                    Map<String, Object> cryptoData = (Map<String, Object>) data.get(symbol);
                    Map<String, Object> quote = (Map<String, Object>) cryptoData.get("quote");
                    Map<String, Object> currencyData = (Map<String, Object>) quote.get(currency);
                    return (Double) currencyData.get("price");
                });
    }

    public Mono<Map<String, Double>> getMultipleCryptoPrices(List<String> symbols, String currency) {
        return fetchCryptoData(symbols, currency)
                .map(data -> symbols.stream().collect(Collectors.toMap(
                        symbol -> symbol,
                        symbol -> {
                            Map<String, Object> cryptoData = (Map<String, Object>) data.get(symbol);
                            Map<String, Object> quote = (Map<String, Object>) cryptoData.get("quote");
                            Map<String, Object> currencyData = (Map<String, Object>) quote.get(currency);
                            return (Double) currencyData.get("price");
                        }
                )));
    }

    private Mono<Map<String, Object>> fetchCryptoData(List<String> symbols, String currency) {
        String symbolParam = String.join(",", symbols);

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("symbol", symbolParam)
                        .queryParam("convert", currency)
                        .build())
                .header("X-CMC_PRO_API_KEY", apiKey)
                .retrieve()
                .bodyToMono(Map.class)
                .map(response -> (Map<String, Object>) response.get("data"));
    }
}
