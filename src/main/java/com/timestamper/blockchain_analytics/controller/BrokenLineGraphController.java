package com.timestamper.blockchain_analytics.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/brokenlinegraph")
public class BrokenLineGraphController {

    // TODO Main > Node > 우클릭 > 꺾은선 그래프 - 트랜잭션기준 - 일주일 정보
    public void getWeeklyTransactionCountInfo() {
    }

    // TODO Main > Node > 우클릭 > 꺾은선 그래프 - 거래 대금기준 - 일주일 정보
    public void getWeeklyTransactionAmountInfo() {
    }

}
