package com.timestamper.blockchain_analytics.dto.visual.etherscan;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EtherscanResponseDTO {
    private String status;
    private String message;
    private EtherscanResultDTO[] result;
}
