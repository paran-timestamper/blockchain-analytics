//package com.timestamper.blockchain_analytics.service;
//
//import com.timestamper.blockchain_analytics.dto.bargraph.reqeust.MonthTransactionRequestDTO;
//import com.timestamper.blockchain_analytics.dto.bargraph.response.MonthTransactionResponseDTO;
//import com.timestamper.blockchain_analytics.repository.BarGraphRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//@Service
//@RequiredArgsConstructor
//public class BarGraphService {
//
//    private final BarGraphRepository barGraphRepository;
//
//    public MonthTransactionResponseDTO getMonthlyTransactionVolume(MonthTransactionRequestDTO request) {
//        return barGraphRepository.selectMonthlyTransactionVolume(request.getFromAddress(), request.getDate());
//    }
//}
