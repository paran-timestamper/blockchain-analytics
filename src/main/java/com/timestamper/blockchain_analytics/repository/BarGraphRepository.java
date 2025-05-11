package com.timestamper.blockchain_analytics.repository;

import com.timestamper.blockchain_analytics.dto.bargraph.response.MonthTransactionResponseDTO;
import com.timestamper.blockchain_analytics.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BarGraphRepository extends JpaRepository<Transaction, Long> {


    @Query(value = """
        SELECT COUNT(*) AS transactionAmount, time
        FROM (
                 SELECT
                     CASE
                         WHEN EXTRACT(DAY FROM TO_TIMESTAMP(t.timestamp / 1000)) BETWEEN 1 AND 5 THEN '1일 ~ 5일'
                         WHEN EXTRACT(DAY FROM TO_TIMESTAMP(t.timestamp / 1000)) BETWEEN 6 AND 10 THEN '6일 ~ 10일'
                         WHEN EXTRACT(DAY FROM TO_TIMESTAMP(t.timestamp / 1000)) BETWEEN 11 AND 15 THEN '11일 ~ 15일'
                         WHEN EXTRACT(DAY FROM TO_TIMESTAMP(t.timestamp / 1000)) BETWEEN 16 AND 20 THEN '16일 ~ 20일'
                         WHEN EXTRACT(DAY FROM TO_TIMESTAMP(t.timestamp / 1000)) BETWEEN 21 AND 25 THEN '21일 ~ 25일'
                         ELSE
                             '26일 ~ ' || TO_CHAR(
                                     (DATE_TRUNC('month', TO_DATE(:yearMonth || '-01', 'YYYY-MM-DD')) + INTERVAL '1 month - 1 day'),
                                     'DD'
                                         ) || '일'
                         END AS time
                 FROM transaction t
                 WHERE t.fromaddress = :fromAddress
                   AND TO_TIMESTAMP(t.timestamp / 1000) >= DATE_TRUNC('month', TO_DATE(:yearMonth || '-01', 'YYYY-MM-DD'))
                   AND TO_TIMESTAMP(t.timestamp / 1000) < (DATE_TRUNC('month', TO_DATE(:yearMonth || '-01', 'YYYY-MM-DD')) + INTERVAL '1 month')
             ) grouped
        GROUP BY time
        ORDER BY
            CASE
                WHEN time = '1일 ~ 5일' THEN 1
                WHEN time = '6일 ~ 10일' THEN 2
                WHEN time = '11일 ~ 15일' THEN 3
                WHEN time = '16일 ~ 20일' THEN 4
                WHEN time = '21일 ~ 25일' THEN 5
                ELSE 6
                END
        """, nativeQuery = true)
    List<Object[]> selectMonthlyTransactionVolume(
            @Param("fromAddress") String fromAddress,
            @Param("yearMonth") String yearMonth
    );

    @Query(value = """
        SELECT COUNT(*) AS transactionAmount, timeRange time
        FROM (
                 SELECT
                     CASE
                         WHEN EXTRACT(HOUR FROM TO_TIMESTAMP(t.timestamp / 1000)) BETWEEN 0 AND 3 THEN '00:00 ~ 03:59'
                         WHEN EXTRACT(HOUR FROM TO_TIMESTAMP(t.timestamp / 1000)) BETWEEN 4 AND 7 THEN '04:00 ~ 07:59'
                         WHEN EXTRACT(HOUR FROM TO_TIMESTAMP(t.timestamp / 1000)) BETWEEN 8 AND 11 THEN '08:00 ~ 11:59'
                         WHEN EXTRACT(HOUR FROM TO_TIMESTAMP(t.timestamp / 1000)) BETWEEN 12 AND 15 THEN '12:00 ~ 15:59'
                         WHEN EXTRACT(HOUR FROM TO_TIMESTAMP(t.timestamp / 1000)) BETWEEN 16 AND 19 THEN '16:00 ~ 19:59'
                         ELSE '20:00 ~ 23:59'
                         END AS timeRange
                 FROM transaction t
                 WHERE t.fromaddress = :fromAddress
                   AND TO_TIMESTAMP(t.timestamp / 1000) >= TO_DATE(:time, 'YYYY-MM-DD')
                   AND TO_TIMESTAMP(t.timestamp / 1000) <  (TO_DATE(:time, 'YYYY-MM-DD') + INTERVAL '1 day')
             ) grouped
        GROUP BY timeRange
        ORDER BY
            CASE
                WHEN timeRange = '00:00 ~ 03:59' THEN 1
                WHEN timeRange = '04:00 ~ 07:59' THEN 2
                WHEN timeRange = '08:00 ~ 11:59' THEN 3
                WHEN timeRange = '12:00 ~ 15:59' THEN 4
                WHEN timeRange = '16:00 ~ 19:59' THEN 5
                ELSE 6
                END
        """, nativeQuery = true)
    List<Object[]> selectDailyTransactionVolume(String fromAddress, String time);
}
