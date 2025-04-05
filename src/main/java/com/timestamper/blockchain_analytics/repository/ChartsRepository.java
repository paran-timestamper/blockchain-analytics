package com.timestamper.blockchain_analytics.repository;

import com.timestamper.blockchain_analytics.dto.charts.TransactionInfoDTO;
import com.timestamper.blockchain_analytics.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface ChartsRepository extends PagingAndSortingRepository<Transaction, Long> {

    // 최신순 필터링
    @Query("""
    SELECT new com.timestamper.blockchain_analytics.dto.charts.TransactionInfoDTO(
        t.txhash, t.fromAddress, t.toAddress, t.amount, t.dpdenom, t.convertedDate
    )
    FROM Transaction t
    WHERE t.dpdenom = :token
    ORDER BY t.timestamp DESC
    """)
    Page<TransactionInfoDTO> findFilteredTransactionsByLatest(
            @Param("token") String token,
            Pageable pageable
    );

    // 최신순 & 날짜 필터링
    @Query("""
        SELECT new com.timestamper.blockchain_analytics.dto.charts.TransactionInfoDTO(
            t.txhash, t.fromAddress, t.toAddress, t.amount, t.dpdenom, t.convertedDate
        )
        FROM Transaction t
        WHERE t.dpdenom = :token
          AND t.convertedDate >= :startDate
          AND t.convertedDate < :endDatePlusOne
        ORDER BY t.timestamp DESC
    """)
    Page<TransactionInfoDTO> findFilteredTransactionsByLatestAndDate(
            @Param("token") String token,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDatePlusOne") LocalDateTime endDatePlusOne,
            Pageable pageable
    );
}
