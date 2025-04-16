package com.timestamper.blockchain_analytics.repository.projection.visual;

public interface AddressProjection {
    String getToAddress();
    Double getTotalValue();
    int getTransactionCount();
    String getConvertedDate();
}
