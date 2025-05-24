package com.timestamper.blockchain_analytics.dto.tokenNode.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
public class TokenNodeChainInfoResponseDTO {
    private Map<String, TokenChainData> tokenChains;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TokenChainData {
        private List<ChainRank> topChains;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ChainRank {
        private int rank;
        private String chain;
    }
}
