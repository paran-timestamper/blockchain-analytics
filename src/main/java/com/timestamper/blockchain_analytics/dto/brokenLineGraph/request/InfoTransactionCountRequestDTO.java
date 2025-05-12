package com.timestamper.blockchain_analytics.dto.brokenLineGraph.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;

@Data
public class InfoTransactionCountRequestDTO {
    private String fromAddress;
    private String token;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;
}
