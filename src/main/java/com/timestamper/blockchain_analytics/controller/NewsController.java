package com.timestamper.blockchain_analytics.controller;

import com.timestamper.blockchain_analytics.dto.news.request.KeywordRequestDTO;
import com.timestamper.blockchain_analytics.dto.news.response.NewsResponseDto;
import com.timestamper.blockchain_analytics.utils.api.CryptoNewsAPI;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/news")
@CrossOrigin(origins = "http://localhost:8081/")
public class NewsController {

    private final CryptoNewsAPI cryptoNewsAPI;

    /**
     * @description Main > 뉴스 기사 검색
     * @return NewsResponseDto
     */
    @PostMapping("")
    public NewsResponseDto getNews(@RequestBody KeywordRequestDTO request) {
        return cryptoNewsAPI.getNews(request.getKeyword());
    }
}
