package com.timestamper.blockchain_analytics.dto.visual.reqeust;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.YearMonth;

@Data
@AllArgsConstructor
public class NodeMonthTransactionRequestDTO {
    private String fromAddress;
    private YearMonth date;
}
