package com.timestamper.blockchain_analytics.repository;

import com.timestamper.blockchain_analytics.dto.TransactionFilteredAmountDTO;
import com.timestamper.blockchain_analytics.dto.TransactionFilteredLatestDTO;
import com.timestamper.blockchain_analytics.dto.TransactionFilteredVolumeDTO;
import com.timestamper.blockchain_analytics.entity.Transaction;
import com.timestamper.blockchain_analytics.repository.projection.transaction.MvTopAddressWithChildrenProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Query("""
        SELECT new com.timestamper.blockchain_analytics.dto.TransactionFilteredVolumeDTO(t.fromAddress, COUNT(t))
        FROM Transaction t
        WHERE t.dpdenom = :token
        GROUP BY t.fromAddress
        ORDER BY COUNT(t) DESC
    """)
    List<TransactionFilteredVolumeDTO> findByTransactionFilteredVolume(String token);

    @Query("""
        SELECT new com.timestamper.blockchain_analytics.dto.TransactionFilteredLatestDTO(
            t.txhash, t.fromAddress, t.toAddress, t.amount, t.dpdenom, t.timestamp)
        FROM Transaction t
        WHERE t.dpdenom = :token
        ORDER BY t.timestamp DESC
        LIMIT 100
    """)
    List<TransactionFilteredLatestDTO> findByTransactionFilteredLatest(@Param("token") String token);

    @Query("""
            SELECT new com.timestamper.blockchain_analytics.dto.TransactionFilteredAmountDTO(
                t.fromAddress, SUM(t.amount))
            FROM Transaction t
            WHERE t.dpdenom = :token
            GROUP BY t.fromAddress
            ORDER BY SUM(t.amount) DESC
            LIMIT 100
        """)
    List<TransactionFilteredAmountDTO> findByTransactionFilteredAmount(@Param("token") String token);

    List<Transaction> findByDpdenomAndFromAddressOrderByTimestampDesc(String dpdenom, String fromAddress);

    @Query(value = """
        SELECT 
            section_label,
            toaddress,
            token,
            ecosystem,
            nodesize,
            value,
            totalvalue,
            transactionamount,
            second_to_address AS secondToAddress,
            second_token AS secondToken,
            second_ecosystem AS secondEcosystem,
            second_value AS secondValue,
            second_totalvalue AS secondTotalValue,
            second_nodesize AS secondNodeSize
        FROM mv_token_top_addresses_with_children
        ORDER BY section_label, totalvalue DESC
    """, nativeQuery = true)
    List<MvTopAddressWithChildrenProjection> findAllTopAddressWithChildren();

    @Query(value = """
        SELECT 
            section_label,
            toaddress,
            token,
            ecosystem,
            nodesize,
            value,
            totalvalue,
            transactionamount,
            second_to_address AS secondToAddress,
            second_token AS secondToken,
            second_ecosystem AS secondEcosystem,
            second_value AS secondValue,
            second_totalvalue AS secondTotalValue,
            second_nodesize AS secondNodeSize
        FROM mv_top100_with_children_address
        ORDER BY section_label, totalvalue DESC
    """, nativeQuery = true)
    List<MvTopAddressWithChildrenProjection> findAllTopAddressWithChildrenForAddress(String fromAddress);
}
