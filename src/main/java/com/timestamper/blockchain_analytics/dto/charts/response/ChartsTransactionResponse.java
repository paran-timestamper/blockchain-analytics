package com.timestamper.blockchain_analytics.dto.charts.response;

import com.timestamper.blockchain_analytics.dto.charts.TransactionInfoDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ChartsTransactionResponse {
    private List<TransactionInfoDTO> transactions;
    private int page;
    private int size;
    private long totalCount;
}
