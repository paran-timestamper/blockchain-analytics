package com.timestamper.blockchain_analytics.repository.projection.transaction;

import java.math.BigDecimal;

public interface MvTopAddressWithChildrenProjection {
    String getSectionLabel();
    String getToAddress();
    String getToken();
    String getEcosystem();
    int getNodeSize();
    BigDecimal getValue();
    BigDecimal getTotalValue();
    int getTransactionAmount();

    String getSecondToAddress();
    String getSecondToken();
    String getSecondEcosystem();
    BigDecimal getSecondValue();
    BigDecimal getSecondTotalValue();
    Integer getSecondNodeSize();
}
