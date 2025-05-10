package com.timestamper.blockchain_analytics.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TransactionFilteredVolumeDTO {
    private String fromAddress;
    private Long count;
}
