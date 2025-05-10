package com.timestamper.blockchain_analytics.enums;

public enum Filter {
    LATEST("최신순"),
    VALUE("거래대금"),
    TRANSACTION("트랜잭션량")

    ;

    private final String description;

    Filter(String description) {
        this.description = description;
    }

}
