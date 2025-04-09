package com.timestamper.blockchain_analytics.repository;

import com.timestamper.blockchain_analytics.entity.Transaction;
import com.timestamper.blockchain_analytics.repository.projection.visual.AddressProjection;
import com.timestamper.blockchain_analytics.repository.projection.visual.NodeInfoProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VisualRepository extends JpaRepository<Transaction, Long> {


    @Query(value = """
        SELECT txhash, type, toaddress
        FROM transaction
        WHERE fromaddress = :fromAddress
        ORDER BY timestamp DESC
        LIMIT 10
    """, nativeQuery = true)
    List<NodeInfoProjection> getNodeInfos(String fromAddress);

    List<Transaction> findByFromAddressAndConvertedDateBetween(String fromAddress, LocalDateTime startOfDay, LocalDateTime endOfDay);

    @Query(value = """
    SELECT TOADDRESS, SUM(AMOUNT) AS TOTAL_VALUE,
           COUNT(TOADDRESS) AS TRANSACTION_AMOUNT, MAX(CONVERTED_DATE) AS CONVERTED_DATE
    FROM TRANSACTION
    WHERE DPDENOM = :token
      AND (:fromAddress IS NULL OR fromaddress = :fromAddress)
    GROUP BY TOADDRESS
    ORDER BY CONVERTED_DATE DESC
    LIMIT 100
""", nativeQuery = true)
    List<AddressProjection> getNodeInfosByLatest(@Param("token") String token, @Param("fromAddress") String fromAddress);
}
