package com.timestamper.blockchain_analytics.repository;

import com.timestamper.blockchain_analytics.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.zip.ZipFile;

public interface BrokenLineGraphRepository extends JpaRepository<Transaction, Long> {

    @Query(value = """
        SELECT
            TO_CHAR(TO_TIMESTAMP(t.timestamp / 1000), 'DD') || '일' AS day,
            COUNT(*) AS txCount
        FROM transaction t
        WHERE t.fromaddress = :fromAddress
          AND t.dpdenom = :token
          AND TO_TIMESTAMP(t.timestamp / 1000) >= TO_DATE(:date, 'YYYY-MM-DD') - INTERVAL '6 days'
          AND TO_TIMESTAMP(t.timestamp / 1000) <  TO_DATE(:date, 'YYYY-MM-DD') + INTERVAL '1 day'
        GROUP BY day
        ORDER BY day
    """, nativeQuery = true)
    List<Object[]> selectWeeklyTransactionCount(
            @Param("fromAddress") String fromAddress,
            @Param("token") String token,
            @Param("date") String date
    );


    @Query(value = """
    SELECT
        TO_CHAR(TO_TIMESTAMP(t.timestamp / 1000), 'DD') || '일' AS day,
        SUM(t.amount * tk.price) AS totalValue
    FROM transaction t
             JOIN token tk ON t.dpdenom = tk.token
    WHERE t.fromaddress = :fromAddress
      AND t.dpdenom = :token
      AND TO_TIMESTAMP(t.timestamp / 1000) >= TO_DATE(:date, 'YYYY-MM-DD') - INTERVAL '6 days'
      AND TO_TIMESTAMP(t.timestamp / 1000) <  TO_DATE(:date, 'YYYY-MM-DD') + INTERVAL '1 day'
    GROUP BY day
    ORDER BY day;
""", nativeQuery = true)
    List<Object[]> selectWeeklyTransactionVolume(
            @Param("fromAddress") String fromAddress,
            @Param("token") String token,
            @Param("date") String date
    );
}
