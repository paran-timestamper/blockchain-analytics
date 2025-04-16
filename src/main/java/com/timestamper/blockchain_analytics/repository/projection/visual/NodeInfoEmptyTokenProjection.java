package com.timestamper.blockchain_analytics.repository.projection.visual;

public interface NodeInfoEmptyTokenProjection {
    String getTxHash();
    String getType();
    String getToAddress();
    Double getAmount();
    String getDpdenom();
}
