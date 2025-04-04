package com.timestamper.blockchain_analytics.service.crypto;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class CryptoNewsService {

    private final static String keyword = "bitcoin";

    @Value("${gnews.api.key}")
    private String apiKey;

    @Value("${gnews.api.base-url}")
    private String baseUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public String getNews() {
        String url = UriComponentsBuilder.fromUriString(baseUrl)
                .queryParam("q", keyword)
                .queryParam("lang", "en")
                .queryParam("max", 10)
                .queryParam("token", apiKey)
                .toUriString();

        return restTemplate.getForObject(url, String.class);
    }
}
