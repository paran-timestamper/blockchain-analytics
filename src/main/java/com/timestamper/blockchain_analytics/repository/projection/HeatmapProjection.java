package com.timestamper.blockchain_analytics.repository.projection;

public interface HeatmapProjection {
    String getDay();
    Integer getHour();
    Integer getTxCount();
}
