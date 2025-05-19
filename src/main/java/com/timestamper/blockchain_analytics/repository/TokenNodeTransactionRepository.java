package com.timestamper.blockchain_analytics.repository;

import com.timestamper.blockchain_analytics.entity.Transaction;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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
}
