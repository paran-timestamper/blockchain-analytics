package com.timestamper.blockchain_analytics.dto.visual;

import com.timestamper.blockchain_analytics.enums.DaySlot;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NodeDayTransactionDTO {
    private Double transactionAmount;
    private DaySlot time;
}
