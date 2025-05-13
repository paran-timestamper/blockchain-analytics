package com.timestamper.blockchain_analytics.repository.projection.node;

public interface NodeTransactionProjection {
    String getTxHash();
    String getDirection();
    String getToAddress();
    Double getAmount();
}
