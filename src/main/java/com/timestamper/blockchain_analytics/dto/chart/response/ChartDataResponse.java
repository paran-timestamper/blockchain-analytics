package com.timestamper.blockchain_analytics.dto.chart.response;

import com.timestamper.blockchain_analytics.enums.Filter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChartDataResponse {

    private List<TransactionResponseDTO> transactions;
    private Filter filter;
    private int page;
    private int size;
    private int totalCount;
}
