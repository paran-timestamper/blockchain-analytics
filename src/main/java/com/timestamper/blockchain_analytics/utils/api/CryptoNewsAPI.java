package com.timestamper.blockchain_analytics.utils.api;

import com.timestamper.blockchain_analytics.dto.news.response.NewsResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class CryptoNewsAPI {

    @Value("${gnews.api.key}")
    private String apiKey;

    @Value("${gnews.api.base-url}")
    private String baseUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public NewsResponseDto getNews(String keyword) {
        String url = UriComponentsBuilder.fromUriString(baseUrl)
                .queryParam("q", keyword)
                .queryParam("lang", "en")
                .queryParam("max", 4)
                .queryParam("sortby", "publishedAt")
                .queryParam("token", apiKey)
                .toUriString();

        return restTemplate.getForObject(url, NewsResponseDto.class);
    }
}
