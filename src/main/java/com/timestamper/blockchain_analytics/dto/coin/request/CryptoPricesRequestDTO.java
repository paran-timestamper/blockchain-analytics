package com.timestamper.blockchain_analytics.dto.coin.request;

import lombok.Data;

import java.util.List;

@Data
public class CryptoPricesRequestDTO {
    private List<String> symbols;
    private String currency;
}
