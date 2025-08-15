package com.timestamper.blockchain_analytics.dto.tokenNode.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.YearMonth;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TokenNodeMonthlyVolumeRequestDTO {
    private List<String> tokens;

    @JsonFormat(pattern = "yyyy-MM")
    private YearMonth date;
}
