package com.timestamper.blockchain_analytics.dto.node.request;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.timestamper.blockchain_analytics.enums.Filter;
import lombok.Data;

@Data
public class NodeInfosRequestDTO {
    private String token;
    private String fromAddress;

    @JsonSetter(nulls = Nulls.SKIP)
    private Filter filter = Filter.LATEST;
}
