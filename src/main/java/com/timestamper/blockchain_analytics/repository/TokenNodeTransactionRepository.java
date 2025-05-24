package com.timestamper.blockchain_analytics.repository;

import com.timestamper.blockchain_analytics.entity.Transaction;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TokenNodeTransactionRepository extends JpaRepository<Transaction, Long> {

    @Query("""
        SELECT 
            t.txhash,
            t.fromAddress,
            t.toAddress,
            (t.amount * 1.0 * tok.price),
            t.timestamp
        FROM Transaction t
        JOIN Token tok ON t.dpdenom = tok.token
        WHERE tok.token = :token
          AND (:cursor IS NULL OR t.timestamp < :cursor)
        ORDER BY t.timestamp DESC
    """)
    List<Object[]> findTopNTransactionsByToken(
            @Param("token") String token,
            @Param("cursor") String cursor,
            Pageable pageable
    );

    @Query(value = """
        SELECT 
            dpdenom,
            day,
            total_amount
        FROM mv_token_daily_amount
        WHERE dpdenom IN :tokens
          AND day BETWEEN :start AND :end
        ORDER BY day
        """, nativeQuery = true)
    List<Object[]> findMonthlyTransactions(
            @Param("tokens") List<String> tokens,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end
    );

    @Query(value = """
        SELECT 
            dpdenom, 
            TO_CHAR(month, 'YYYY.MM') AS year_month, 
            total_amount
        FROM mv_total_token_volume
        WHERE dpdenom IN :tokens
        ORDER BY year_month
        """, nativeQuery = true)
    List<Object[]> findTotalVolumeByTokens(@Param("tokens") List<String> tokens);

    @Query(value = """
        SELECT 
            dpdenom,
            hour_block,
            total_amount
        FROM mv_token_hourly_volume
        WHERE dpdenom IN :tokens
          AND day = :targetDate
        ORDER BY hour_block
        """, nativeQuery = true)
    List<Object[]> findHourlyVolumeByTokensFromView(
            @Param("tokens") List<String> tokens,
            @Param("targetDate") LocalDate targetDate
    );

    @Query(value = """
        WITH ranked_tx AS (
            SELECT
                dpdenom,
                fromchain,
                COUNT(*) AS cnt,
                ROW_NUMBER() OVER (PARTITION BY dpdenom ORDER BY COUNT(*) DESC) AS rn
            FROM transaction
            WHERE dpdenom IN :tokens
              AND fromchain IS NOT NULL
            GROUP BY dpdenom, fromchain
        )
        SELECT dpdenom, fromchain, rn
        FROM ranked_tx
        WHERE rn <= 10
        ORDER BY dpdenom, rn;
        """, nativeQuery = true)
    List<Object[]> selectTopChains(@Param("tokens") List<String> tokens);
}
