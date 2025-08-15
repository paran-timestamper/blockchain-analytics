package com.timestamper.blockchain_analytics.dto.heatmap.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.YearMonth;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyInfoRequestDTO {
    private String fromAddress;

    @JsonFormat(pattern = "yyyy-MM")
    private YearMonth date;
}
