package com.timestamper.blockchain_analytics.repository.projection.visual;

public interface NodeInfoProjection {
    String getTxHash();
    String getType();
    String getToAddress();
}
