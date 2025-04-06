package com.timestamper.blockchain_analytics.service.projection;

public interface HeatmapTransactionProjection {
    Integer getRank();
    String getToken();
    Long getTransactionCount();
    Double getTotalAmount();
}
