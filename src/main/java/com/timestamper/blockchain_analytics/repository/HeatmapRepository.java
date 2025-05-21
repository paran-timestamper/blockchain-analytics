package com.timestamper.blockchain_analytics.repository;

import com.timestamper.blockchain_analytics.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HeatmapRepository extends JpaRepository<Transaction, Long> {

    @Query(value = """
        SELECT
            TO_CHAR(TO_TIMESTAMP(t.timestamp / 1000), 'DD') || '일' AS day,
            COUNT(*) AS txCount
        FROM transaction t
        WHERE t.fromaddress = :fromAddress
          AND TO_TIMESTAMP(t.timestamp / 1000) >= TO_DATE(:startDate, 'YYYY-MM-DD')
          AND TO_TIMESTAMP(t.timestamp / 1000) <  TO_DATE(:startDate, 'YYYY-MM-DD') + INTERVAL '1 month'
        GROUP BY day
        ORDER BY day
    """, nativeQuery = true)
    List<Object[]> selectMonthlyInfo(
            @Param("fromAddress") String fromAddress,
            @Param("startDate") String startDate
    );

    @Query(value = """
        SELECT
            grouped.range,
            COUNT(*) AS txCount
        FROM (
            SELECT
                CASE
                    WHEN EXTRACT(HOUR FROM TO_TIMESTAMP(t.timestamp / 1000)) BETWEEN 0 AND 3 THEN '00-04'
                    WHEN EXTRACT(HOUR FROM TO_TIMESTAMP(t.timestamp / 1000)) BETWEEN 4 AND 7 THEN '04-08'
                    WHEN EXTRACT(HOUR FROM TO_TIMESTAMP(t.timestamp / 1000)) BETWEEN 8 AND 11 THEN '08-12'
                    WHEN EXTRACT(HOUR FROM TO_TIMESTAMP(t.timestamp / 1000)) BETWEEN 12 AND 15 THEN '12-16'
                    WHEN EXTRACT(HOUR FROM TO_TIMESTAMP(t.timestamp / 1000)) BETWEEN 16 AND 19 THEN '16-20'
                    ELSE '20-24'
                END AS range
            FROM transaction t
            WHERE t.fromaddress = :fromAddress
              AND TO_TIMESTAMP(t.timestamp / 1000) >= TO_DATE(:date, 'YYYY-MM-DD')
              AND TO_TIMESTAMP(t.timestamp / 1000) <  TO_DATE(:date, 'YYYY-MM-DD') + INTERVAL '1 day'
        ) grouped
        GROUP BY grouped.range
        ORDER BY
            CASE grouped.range
                WHEN '00-04' THEN 1
                WHEN '04-08' THEN 2
                WHEN '08-12' THEN 3
                WHEN '12-16' THEN 4
                WHEN '16-20' THEN 5
                ELSE 6
            END
    """, nativeQuery = true)
    List<Object[]> selectDailyInfo(
            @Param("fromAddress") String fromAddress,
            @Param("date") String date
    );

    @Query(value = """
        SELECT
            t.dpdenom,
            COUNT(*) AS transactionCount,
            SUM(t.amount) AS totalAmount,
            COALESCE(p.price, 0) AS currentMarketPrice
        FROM transaction t
                 LEFT JOIN token p ON t.dpdenom = p.token
        WHERE t.fromaddress = :fromAddress
          AND t.dpdenom <> 'Unknown'
          AND TO_TIMESTAMP(t.timestamp / 1000) >= TO_DATE(:startDate, 'YYYY-MM-DD')
          AND TO_TIMESTAMP(t.timestamp / 1000) <  TO_DATE(:startDate, 'YYYY-MM-DD') + INTERVAL '1 day'
        GROUP BY t.dpdenom, COALESCE(p.price, 0), p.price
        ORDER BY transactionCount DESC
        limit 3
    """, nativeQuery = true)
    List<Object[]> selectMonthlyDetail(
            @Param("fromAddress") String fromAddress,
            @Param("startDate") String startDate
    );

    @Query(value = """
        SELECT
            t.dpdenom,
            COUNT(*) AS transactionCount,
            SUM(t.amount) AS totalAmount,
            COALESCE(p.price, 0) AS currentMarketPrice
        FROM transaction t
                 LEFT JOIN token p ON t.dpdenom = p.token
        WHERE t.fromaddress = :fromAddress
          AND t.dpdenom <> 'Unknown'
          AND TO_TIMESTAMP(t.timestamp / 1000) >= TO_TIMESTAMP(:startDateTime, 'YYYY-MM-DD HH24:MI:SS')
          AND TO_TIMESTAMP(t.timestamp / 1000) <  TO_TIMESTAMP(:endDateTime, 'YYYY-MM-DD HH24:MI:SS')
        GROUP BY t.dpdenom, p.price
        ORDER BY transactionCount DESC
        LIMIT 3
        """, nativeQuery = true)
    List<Object[]> selectDailyDetail(
            @Param("fromAddress") String fromAddress,
            @Param("startDateTime") String startDateTime,
            @Param("endDateTime") String endDateTime
    );
}
