package com.timestamper.blockchain_analytics.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/node")
public class NodeController {

    // Main > Node > TOP1 Address 출력
    public void getTOP1Address() {

    }

    // Main > Node > 좌클릭 > TOKEN, FILTER를 기준으로  클릭된 노드와 트랜잭션을 발생시킨 노드 10개를 추가로 생성 및 브랜치 확장.
    public void getNodeInfos() {

    }

    // Main > Node > 우클릭 > 노드 트랜잭션 내역(txhash,direction,to address, value) - paging 적용
    public void getNodeTransaction() {

    }
}
