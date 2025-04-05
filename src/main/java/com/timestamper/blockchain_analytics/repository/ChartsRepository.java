package com.timestamper.blockchain_analytics.repository;

import com.timestamper.blockchain_analytics.dto.charts.ChartsFilteredAmountDTO;
import com.timestamper.blockchain_analytics.dto.charts.ChartsFilteredLatestDTO;
import com.timestamper.blockchain_analytics.dto.charts.ChartsFilteredVolumeDTO;
import com.timestamper.blockchain_analytics.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChartsRepository extends JpaRepository<Transaction, Long> {

    @Query("""
        SELECT new com.timestamper.blockchain_analytics.dto.charts.ChartsFilteredVolumeDTO(t.fromAddress, COUNT(t))
        FROM Transaction t
        WHERE t.dpdenom = :token
        GROUP BY t.fromAddress
        ORDER BY COUNT(t) DESC
    """)
    List<ChartsFilteredVolumeDTO> findByChartsFilteredVolume(String token);

    @Query("""
        SELECT new com.timestamper.blockchain_analytics.dto.charts.ChartsFilteredLatestDTO(
            t.txhash, t.fromAddress, t.toAddress, t.amount, t.dpdenom, t.timestamp)
        FROM Transaction t
        WHERE t.dpdenom = :token
        ORDER BY t.timestamp DESC
        LIMIT 100
    """)
    List<ChartsFilteredLatestDTO> findByChartsFilteredLatest(@Param("token") String token);

    @Query("""
            SELECT new com.timestamper.blockchain_analytics.dto.charts.ChartsFilteredAmountDTO(
                t.fromAddress, SUM(t.amount))
            FROM Transaction t
            WHERE t.dpdenom = :token
            GROUP BY t.fromAddress
            ORDER BY SUM(t.amount) DESC
            LIMIT 100
        """)
    List<ChartsFilteredAmountDTO> findByChartsFilteredAmount(@Param("token") String token);

    List<Transaction> findByDpdenomAndFromAddressOrderByTimestampDesc(String dpdenom, String fromAddress);

}
