package com.timestamper.blockchain_analytics.dto.bargraph.reqeust;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;

@Data
public class DailyTransactionRequestDTO {
    private String fromAddress;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;
}
