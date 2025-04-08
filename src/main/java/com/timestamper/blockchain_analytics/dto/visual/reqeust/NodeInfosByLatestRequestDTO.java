package com.timestamper.blockchain_analytics.dto.visual.reqeust;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NodeInfosByLatestRequestDTO {
    private String token;
    private String fromAddress;
}
