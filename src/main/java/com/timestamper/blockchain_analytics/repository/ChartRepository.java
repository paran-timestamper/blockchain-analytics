package com.timestamper.blockchain_analytics.repository;

import com.timestamper.blockchain_analytics.entity.Transaction;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChartRepository extends PagingAndSortingRepository<Transaction, Long> {

    @Query("""
        SELECT t, p.price
        FROM Transaction t
        JOIN Token p ON p.token = t.dpdenom
        WHERE (:token IS NULL OR t.dpdenom = :token)
          AND (:fromAddress IS NULL OR t.fromAddress = :fromAddress)
        ORDER BY t.convertedDate DESC
    """)
    List<Object[]> findByLatest(@Param("token") String token,
                                @Param("fromAddress") String fromAddress,
                                Pageable pageable);

    @Query("""
        SELECT t, p.price
        FROM Transaction t
        JOIN Token p ON p.token = t.dpdenom
        WHERE (:token IS NULL OR t.dpdenom = :token)
          AND (:fromAddress IS NULL OR t.fromAddress = :fromAddress)
        ORDER BY p.price * t.amount DESC
    """)
    List<Object[]> findByValue(@Param("token") String token,
                               @Param("fromAddress") String fromAddress,
                               Pageable pageable);

    @Query("""
        SELECT COUNT(t)
        FROM Transaction t
        WHERE (:token IS NULL OR t.dpdenom = :token)
          AND (:fromAddress IS NULL OR t.fromAddress = :fromAddress)
    """)
    int countFiltered(@Param("token") String token, @Param("fromAddress") String fromAddress);
}
