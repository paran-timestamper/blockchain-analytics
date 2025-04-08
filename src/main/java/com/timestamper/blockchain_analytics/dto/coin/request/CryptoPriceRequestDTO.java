package com.timestamper.blockchain_analytics.dto.coin.request;

import lombok.Data;

@Data
public class CryptoPriceRequestDTO {
    private String symbols;
    private String currency;
}
