package com.timestamper.blockchain_analytics.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    @Column(nullable = false)
    private String fromAddress;

    @Column(nullable = false)
    private String toAddress;

    @Column(nullable = false)
    private String fromChain;

    @Column(nullable = false)
    private String toChain;

    public void BlockchainTransaction(String txhash, String type, Long timestamp, Double amount, String denom, String dpdenom,
                                      String fromAddress, String toAddress, String fromChain, String toChain) {
        this.txhash = txhash;
        this.type = type;
        this.timestamp = timestamp;
        this.amount = amount;
        this.denom = denom;
        this.dpdenom = dpdenom;
        this.fromAddress = fromAddress;
        this.toAddress = toAddress;
        this.fromChain = fromChain;
        this.toChain = toChain;
    }
}
