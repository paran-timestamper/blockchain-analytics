package com.timestamper.blockchain_analytics.dto.transaction.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionGraphResponseDTO {
    private String fromAddress;
    private List<SectionData> sections;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SectionData {
        private String sectionDate;
        private List<TokenData> tokens;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TokenData {
        private String token;
        private String ecosystem;
        private int nodeSize;
        private BigDecimal value;
        private BigDecimal totalValue;
        private int transactionAmount;
        private String toAddress;
        private List<SecondToAddressDTO> secondToAddresses;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SecondToAddressDTO {
        private String token;
        private String ecosystem;
        private int nodeSize;
        private BigDecimal value;
    }
}
