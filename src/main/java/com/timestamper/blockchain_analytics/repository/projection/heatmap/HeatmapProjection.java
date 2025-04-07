package com.timestamper.blockchain_analytics.repository.projection.heatmap;

public interface HeatmapProjection {
    String getDay();
    Integer getHour();
    Integer getTxCount();
}
