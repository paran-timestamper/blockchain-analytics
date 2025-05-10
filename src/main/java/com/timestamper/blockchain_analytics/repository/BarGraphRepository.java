//package com.timestamper.blockchain_analytics.repository;
//
//import com.timestamper.blockchain_analytics.dto.bargraph.response.MonthTransactionResponseDTO;
//import com.timestamper.blockchain_analytics.entity.Transaction;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//
//import java.time.YearMonth;
//
//public interface BarGraphRepository extends JpaRepository<Transaction, Long> {
//
//
//    @Query
//    MonthTransactionResponseDTO selectMonthlyTransactionVolume(String fromAddress, YearMonth date);
//}
