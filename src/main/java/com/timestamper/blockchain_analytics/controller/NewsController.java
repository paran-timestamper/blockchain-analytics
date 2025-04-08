package com.timestamper.blockchain_analytics.controller;

import com.timestamper.blockchain_analytics.dto.main.news.response.NewsResponseDto;
import com.timestamper.blockchain_analytics.utils.api.CryptoNewsAPI;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/news")
public class NewsController {

    private final CryptoNewsAPI cryptoNewsAPI;

    /**
     * @description Main > 뉴스 기사 검색
     * @return NewsResponseDto
     */
    @GetMapping("/search")
    public NewsResponseDto getNews(@RequestParam String query) {
        System.out.println("검색어: " + query);
        return cryptoNewsAPI.getNews(query);
    }

}
