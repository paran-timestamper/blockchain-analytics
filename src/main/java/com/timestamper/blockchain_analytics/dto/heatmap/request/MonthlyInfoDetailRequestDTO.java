package com.timestamper.blockchain_analytics.dto.heatmap.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class MonthlyInfoDetailRequestDTO {
    private String fromAddress;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;
}
