package com.timestamper.blockchain_analytics.repository.projection.visual;

public interface AddressAddDpdenomProjection {
    String getToAddress();
    Double getTotalValue();
    Integer getTransactionCount();
    String getConvertedDate();
    String getDpdenom();
}
