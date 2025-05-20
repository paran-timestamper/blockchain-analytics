package com.timestamper.blockchain_analytics.dto.tokenNode.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenNodeGraphResponseDTO {
    private List<SectionData> sections;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SectionData {
        private String sectionDate;
        private List<TokenData> tokens;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TokenData {
        private String token;
        private String ecosystem;
        private int colorDarkening;
        private String timestamp;
        private String generationDate;
        private int nodeSize;
        private String totalValue;
        private String transactionAmount;
    }
}
