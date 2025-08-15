package com.timestamper.blockchain_analytics.service.crypto;

import com.timestamper.blockchain_analytics.entity.Token;
import com.timestamper.blockchain_analytics.repository.TokenRepository;
import com.timestamper.blockchain_analytics.repository.TransactionRepository;
import com.timestamper.blockchain_analytics.service.CommonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuples;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.ThreadUtils.sleep;

@Slf4j
@Service
public class CryptoPriceService {
    private final WebClient webClient;
    private final CommonService commonService;
    private final TokenRepository tokenRepository;

    @Value("${coinmarketcap.api.key}")
    private String apiKey;

    private static final String BASE_URL = "https://pro-api.coinmarketcap.com/v1/cryptocurrency/quotes/latest";

    public CryptoPriceService(WebClient.Builder webClientBuilder, CommonService commonService, TransactionRepository transactionRepository, TokenRepository tokenRepository) {
        this.webClient = webClientBuilder.baseUrl(BASE_URL).build();
        this.commonService = commonService;
        this.tokenRepository = tokenRepository;
    }

    public Mono<Double> getCryptoPrice(String symbol, String currency) {
        return fetchCryptoData(List.of(symbol), currency)
                .map(data -> {
                    Object price = Optional.ofNullable(data)
                            .map(d -> (Map<String, Object>) d.get(symbol))
                            .map(d -> (Map<String, Object>) d.get("quote"))
                            .map(d -> (Map<String, Object>) d.get(currency))
                            .map(d -> d.get("price"))
                            .orElse(0.0);

                    return (price instanceof Number) ? ((Number) price).doubleValue() : 0.0;
                })
                .onErrorResume(e -> {
                    log.error("💥 WebClient error for symbol {}: {}", symbol, e.getMessage());
                    return Mono.just(0.0);
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

    public void updateCryptoPrices() {
        List<String> allTokens = commonService.getAllTokens();

        allTokens.forEach(token -> {
            try {
                Mono<Double> priceMono = getCryptoPrice(token, "USD");
                Double price = priceMono.block();  // 블로킹 방식으로 가격 가져옴

                Optional<Token> optionalToken = tokenRepository.findByToken(token);
                if (optionalToken.isEmpty()) {
                    log.warn("Token {} not found in DB", token);
                    return;
                }

                Token tokenEntity = optionalToken.get();
                BigDecimal priceValue = (price == 0.0) ? BigDecimal.ZERO : BigDecimal.valueOf(price);
                tokenEntity.setPrice(priceValue);
                tokenRepository.save(tokenEntity);
                log.info("✅ Updated price for token {}: {}", token, priceValue);

                Thread.sleep(1000);  // 💡 1초 대기

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // 인터럽트 설정 복구
                log.error("⛔ Thread interrupted during sleep for token {}", token);
            } catch (Exception e) {
                log.error("💥 Error updating price for token {}: {}", token, e.getMessage());
            }
        });
    }
}
