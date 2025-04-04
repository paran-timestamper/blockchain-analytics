package com.timestamper.blockchain_analytics.controller;

import com.timestamper.blockchain_analytics.service.crypto.CryptoNewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/main")
public class MainController {

    private final CryptoNewsService cryptoNewsService;

    @GetMapping("/")
    public String getMainInfo() {
        return cryptoNewsService.getNews();
    }
}
