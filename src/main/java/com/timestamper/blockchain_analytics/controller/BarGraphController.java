//package com.timestamper.blockchain_analytics.controller;
//
//import com.timestamper.blockchain_analytics.dto.bargraph.reqeust.DailyTransactionRequestDTO;
//import com.timestamper.blockchain_analytics.dto.bargraph.reqeust.MonthTransactionRequestDTO;
//import com.timestamper.blockchain_analytics.dto.bargraph.response.DailyTransactionResponseDTO;
//import com.timestamper.blockchain_analytics.dto.bargraph.response.MonthTransactionResponseDTO;
//import com.timestamper.blockchain_analytics.service.BarGraphService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequiredArgsConstructor
//@RequestMapping("/api/bargraph")
//public class BarGraphController {
//
//    private final BarGraphService barGraphService;
//
//    // TODO Main > Node > 우클릭 > 월간거래량
//    public MonthTransactionResponseDTO getMonthlyTransactionVolume(MonthTransactionRequestDTO request) {
//        return barGraphService.getMonthlyTransactionVolume(request);
//    }
//
//    // TODO Main > Node > 우클릭 > 일간거래량
//    public DailyTransactionResponseDTO getDailyTransactionVolume(DailyTransactionRequestDTO request) {
//        return null;
//    }
//}
