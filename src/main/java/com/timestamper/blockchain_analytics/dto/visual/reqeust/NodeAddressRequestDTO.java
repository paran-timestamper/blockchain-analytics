package com.timestamper.blockchain_analytics.dto.visual.reqeust;

import jakarta.annotation.Nullable;
import lombok.Getter;

@Getter
public class NodeAddressRequestDTO {
    private String fromAddress;

    @Nullable
    private String token;
}
