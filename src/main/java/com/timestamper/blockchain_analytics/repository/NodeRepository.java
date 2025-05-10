package com.timestamper.blockchain_analytics.repository;

import com.timestamper.blockchain_analytics.dto.node.NodeInfoDTO;
import com.timestamper.blockchain_analytics.entity.Transaction;
import com.timestamper.blockchain_analytics.repository.projection.node.NodeTransactionProjection;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NodeRepository  extends JpaRepository<Transaction, Long> {

    @Query(value = """
        SELECT toaddress, dpdenom
        FROM transaction
        WHERE fromaddress = :fromAddress
        ORDER BY amount DESC
        LIMIT 100
    """, nativeQuery = true)
    List<NodeInfoDTO> findTopToAddressesByFromAddress(String fromAddress);

    @Query(value = """
        SELECT t.toaddress, t.dpdenom
        FROM Transaction t
        WHERE (:fromAddress IS NULL OR t.fromAddress = :fromAddress)
          AND (:token IS NULL OR t.dpdenom = :token)
        ORDER BY t.timestamp DESC
        limit 100
    """, nativeQuery = true)
    List<NodeInfoDTO> findNodeInfosByLatest(String fromAddress, String token);

    @Query(value = """
        SELECT t.toaddress, dpdenom
        FROM Transaction t
        WHERE (:fromAddress IS NULL OR t.fromAddress = :fromAddress)
          AND (:token IS NULL OR t.dpdenom = :token)
        GROUP BY t.toaddress, dpdenom
        ORDER BY COUNT(t) DESC
        LIMIT 100
    """, nativeQuery = true)
    List<NodeInfoDTO> findNodeInfosByTransaction(String fromAddress, String token);

    // TODO 쿼리 만들어야함 거래 대금 기준으로 select하는 쿼리
    @Query(value = """
        SELECT t.toaddress, t.dpdenom
        FROM Transaction t
        WHERE (:fromAddress IS NULL OR t.fromAddress = :fromAddress)
          AND (:token IS NULL OR t.dpdenom = :token)
        ORDER BY t.timestamp DESC
        limit 100
    """, nativeQuery = true)
    List<NodeInfoDTO> findNodeInfosByValue(String fromAddress, String token);

    @Query(value = """
        SELECT t.toaddress, t.dpdenom
        FROM Transaction t
        WHERE (:fromAddress IS NULL OR t.fromAddress = :fromAddress)
          AND (:token IS NULL OR t.dpdenom = :token)
        ORDER BY t.timestamp DESC
        limit 10
    """, nativeQuery = true)
    List<NodeInfoDTO> findNodeInfosByLatestLimit10(String fromAddress, String token);

    @Query(value = """
        SELECT t.toaddress, dpdenom
        FROM Transaction t
        WHERE (:fromAddress IS NULL OR t.fromAddress = :fromAddress)
          AND (:token IS NULL OR t.dpdenom = :token)
        GROUP BY t.toaddress, dpdenom
        ORDER BY COUNT(t) DESC
        LIMIT 10
    """, nativeQuery = true)
    List<NodeInfoDTO> findNodeInfosByTransactionLimit10(String fromAddress, String token);

    // TODO 쿼리 만들어야함 거래 대금 기준으로 select하는 쿼리
    @Query(value = """
        SELECT t.toaddress, t.dpdenom
        FROM Transaction t
        WHERE (:fromAddress IS NULL OR t.fromAddress = :fromAddress)
          AND (:token IS NULL OR t.dpdenom = :token)
        ORDER BY t.timestamp DESC
        limit 10
    """, nativeQuery = true)
    List<NodeInfoDTO> findNodeInfosByValueLimit10(String fromAddress, String token);

    @Query("""
        SELECT t.txhash txHash, t.type direction, t.toAddress toAddress, t.amount amount
        FROM Transaction t
        WHERE t.fromAddress = :fromAddress
          AND t.dpdenom = :token
        ORDER BY t.timestamp DESC
""")
    List<NodeTransactionProjection> findNodeTransaction(
            @Param("fromAddress") String fromAddress,
            @Param("token") String token,
            Pageable pageable
    );
}
