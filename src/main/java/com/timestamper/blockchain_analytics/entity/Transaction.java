package com.timestamper.blockchain_analytics.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "transaction")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String txhash;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private Long timestamp;

    @Column(nullable = false)
    private Double amount;

    @Column(nullable = false)
    private String denom;

    @Column(nullable = false)
    private String dpdenom;

    @Column(nullable = false, name = "fromaddress")
    private String fromAddress;

    @Column(nullable = false, name = "toaddress")
    private String toAddress;

    @Column(nullable = false, name = "fromchain")
    private String fromChain;

    @Column(nullable = false, name = "tochain")
    private String toChain;

    @Column(name = "convertedDate")
    private LocalDateTime convertedDate;
}
