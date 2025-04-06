package com.timestamper.blockchain_analytics.repository;

import com.timestamper.blockchain_analytics.entity.Transaction;
import com.timestamper.blockchain_analytics.service.projection.HeatmapProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface HeatmapRepository extends JpaRepository<Transaction, Long> {

    @Query(value = """
        WITH day_hour_matrix AS (
            SELECT day, hour
            FROM (
                SELECT unnest(ARRAY[
                    'Monday','Tuesday','Wednesday','Thursday','Friday','Saturday','Sunday'
                ]) AS day
            ) d
            CROSS JOIN generate_series(0, 23) AS hour
        ),
        tx_data AS (
            SELECT
                TO_CHAR(t.converted_date, 'FMDay') AS day,
                EXTRACT(HOUR FROM t.converted_date) AS hour,
                COUNT(*) AS txCount
            FROM transaction t
            WHERE t.converted_date >= :startDateTime
              AND t.converted_date < :endDateTime
            GROUP BY day, hour
        ),
        merged AS (
            SELECT
                m.day,
                m.hour,
                COALESCE(td.txCount, 0) AS txCount,
                CASE m.day
                    WHEN 'Monday' THEN 1
                    WHEN 'Tuesday' THEN 2
                    WHEN 'Wednesday' THEN 3
                    WHEN 'Thursday' THEN 4
                    WHEN 'Friday' THEN 5
                    WHEN 'Saturday' THEN 6
                    WHEN 'Sunday' THEN 7
                END AS day_order
            FROM day_hour_matrix m
            LEFT JOIN tx_data td
              ON m.day = td.day AND m.hour = td.hour
        )
        SELECT day, hour, txCount
        FROM merged
        ORDER BY day_order, hour
    """, nativeQuery = true)
    List<HeatmapProjection> getInfo(LocalDateTime startDateTime, LocalDateTime endDateTime);
}
