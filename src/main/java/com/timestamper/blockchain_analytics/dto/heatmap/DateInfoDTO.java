package com.timestamper.blockchain_analytics.dto.heatmap;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DateInfoDTO {
    private String day;
    private int hour;
    private int txCount;
}
