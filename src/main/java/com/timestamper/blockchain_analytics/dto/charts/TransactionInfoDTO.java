package com.timestamper.blockchain_analytics.dto.charts;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class TransactionInfoDTO {
    private String txHash;
    private String fromAddress;
    private String toAddress;
    private double value;
    private double totalPrice;
    private String dpDenom;
    private LocalDateTime convertedDate;
}


