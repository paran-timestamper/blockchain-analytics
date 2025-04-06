package com.timestamper.blockchain_analytics.service.projection;

public interface HeatmapProjection {
    String getDay();
    Integer getHour();
    Integer getTxCount();
}
