package com.timestamper.blockchain_analytics.dto;

import com.timestamper.blockchain_analytics.utils.api.TokenPriceAPIClient;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TransactionFilteredAmountDTO {

    public TransactionFilteredAmountDTO(String fromAddress, Double totalAmount) {
        this.fromAddress = fromAddress;
        this.totalAmount = totalAmount;
        this.totalValue = totalAmount * TokenPriceAPIClient.getPrice();
    }

    private String fromAddress; // 거래 주소
    private Double totalAmount; // 총 거래량
    private Double totalValue;  // 총 거래량 * 현재 토큰 가격
}
