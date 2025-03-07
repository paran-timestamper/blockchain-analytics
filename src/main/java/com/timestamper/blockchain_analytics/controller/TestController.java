package com.timestamper.blockchain_analytics.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequestMapping("/api")
@RestController
public class TestController {

    @GetMapping("test")
    public void getTest() {
        log.info("Execute Test Controller");
    }
}
