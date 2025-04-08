package com.timestamper.blockchain_analytics.dto.visual;

import com.timestamper.blockchain_analytics.enums.MonthSlot;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NodeMonthTransactionDTO {
    private double transactionAmount;
    private MonthSlot monthSlot;
}
