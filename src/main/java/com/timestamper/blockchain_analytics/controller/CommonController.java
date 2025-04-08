package com.timestamper.blockchain_analytics.controller;

import com.timestamper.blockchain_analytics.service.CommonService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/common")
public class CommonController {

    private final CommonService commonService;

    /**
     * Charts, Visual 80개 토큰 조회
     * @return
     */
    @PostMapping("tokens")
    public List<String> getTokens() {
        return commonService.getAllTokens();
    }
}
