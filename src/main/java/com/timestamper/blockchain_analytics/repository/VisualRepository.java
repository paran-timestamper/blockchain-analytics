package com.timestamper.blockchain_analytics.repository;

import com.timestamper.blockchain_analytics.entity.Transaction;
import com.timestamper.blockchain_analytics.repository.projection.visual.NodeInfoProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

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
}
