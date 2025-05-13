package com.timestamper.blockchain_analytics.dto.chart.request;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.timestamper.blockchain_analytics.enums.Filter;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChartDataRequest {
    private int page;
    private int size;
    private String token;
    private String fromAddress;

    @JsonSetter(nulls = Nulls.SKIP)
    private Filter filter;
}
