package com.timestamper.blockchain_analytics.dto.heatmap.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.YearMonth;
import java.util.List;

@Data
@AllArgsConstructor
public class CompareMonthlyRequestDTO {

    private List<String> tokens;

    @JsonFormat(pattern = "yyyy-MM")
    private YearMonth date;
}
