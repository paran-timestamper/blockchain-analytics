package com.timestamper.blockchain_analytics.dto.treemap.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TreemapInfoResponseDTO {
    private String token;
    private Double value;
    private String percentage;
}
