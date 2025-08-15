package com.timestamper.blockchain_analytics.dto.visual.reqeust;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public class NodeDayTransactionRequestDTO {
    private String fromAddress;
    private LocalDate date;
}
