package com.timestamper.blockchain_analytics.repository;

import com.timestamper.blockchain_analytics.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Query("SELECT DISTINCT t.dpdenom FROM Transaction t ORDER BY t.dpdenom")
    List<String> findDistinctDpdenom();
}
