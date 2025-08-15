package com.timestamper.blockchain_analytics.dto.tokenNode.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TokenNodeTransactionRequestDTO {

    private String token;
    private int page;
    private int size;
}
