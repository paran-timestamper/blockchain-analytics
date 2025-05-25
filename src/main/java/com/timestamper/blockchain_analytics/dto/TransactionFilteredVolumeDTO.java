package com.timestamper.blockchain_analytics.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor

public class TransactionFilteredVolumeDTO {
    private String fromAddress;
    private Long count;
}
