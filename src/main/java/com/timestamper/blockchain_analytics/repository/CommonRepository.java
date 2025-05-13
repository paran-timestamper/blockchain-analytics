package com.timestamper.blockchain_analytics.repository;

import com.timestamper.blockchain_analytics.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommonRepository extends JpaRepository<Transaction, Long> {

    @Query("SELECT t.token FROM Token t ORDER BY t.token")
    List<String> findDistinctDpdenom();

}
