package com.timestamper.blockchain_analytics.repository;

import com.timestamper.blockchain_analytics.dto.treemap.response.TreemapInfoResponseDTO;
import com.timestamper.blockchain_analytics.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TreemapRepository extends JpaRepository<Transaction, Long> {

    @Query(value = """
        SELECT 
            t.dpdenom AS token,
            SUM(t.amount) AS value,
            ROUND(SUM(t.amount) * 100.0 / total.total_value, 2) || '%' AS percentage
        FROM transaction t,
             (
                 SELECT SUM(amount) AS total_value
                 FROM transaction
                 WHERE fromaddress = :fromAddress
                   AND dpdenom <> 'Unknown'
             ) total
        WHERE t.fromaddress = :fromAddress
          AND t.dpdenom <> 'Unknown'
        GROUP BY t.dpdenom, total.total_value
        ORDER BY value DESC
        LIMIT 16
        """, nativeQuery = true)
    List<Object[]> selectTreemapInfo(@Param("fromAddress") String fromAddress);
}
