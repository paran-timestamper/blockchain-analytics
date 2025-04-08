package com.timestamper.blockchain_analytics.dto.visual.etherscan;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EtherscanResultDTO {
    private String address;
    private String nametag;
    private String[] labels;
}
