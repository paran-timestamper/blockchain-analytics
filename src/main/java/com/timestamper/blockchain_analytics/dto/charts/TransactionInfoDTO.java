package com.timestamper.blockchain_analytics.dto.charts;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class TransactionInfoDTO {
    private String txHash;
    private String fromAddress;
    private String toAddress;
    private Double value;
    private String dpDenom;
    private LocalDateTime convertedDate;
}
