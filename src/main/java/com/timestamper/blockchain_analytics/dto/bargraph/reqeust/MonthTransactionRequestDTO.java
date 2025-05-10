package com.timestamper.blockchain_analytics.dto.bargraph.reqeust;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.YearMonth;

@Data
public class MonthTransactionRequestDTO {
    private String fromAddress;

    @JsonFormat(pattern = "yyyy-MM")
    private YearMonth date;
}
