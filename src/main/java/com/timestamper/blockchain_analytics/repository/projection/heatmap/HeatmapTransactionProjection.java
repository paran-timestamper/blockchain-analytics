package com.timestamper.blockchain_analytics.repository.projection.heatmap;

public interface HeatmapTransactionProjection {
    Integer getRank();
    String getToken();
    Long getTransactionCount();
    Double getTotalAmount();
}
