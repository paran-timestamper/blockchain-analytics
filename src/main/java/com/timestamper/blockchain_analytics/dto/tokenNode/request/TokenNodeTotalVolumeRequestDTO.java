package com.timestamper.blockchain_analytics.dto.tokenNode.request;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class TokenNodeTotalVolumeRequestDTO {
    private List<String> tokens;
}
