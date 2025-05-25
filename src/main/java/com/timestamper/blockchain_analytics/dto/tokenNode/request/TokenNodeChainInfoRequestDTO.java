package com.timestamper.blockchain_analytics.dto.tokenNode.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TokenNodeChainInfoRequestDTO {
    private List<String> tokens;
}
