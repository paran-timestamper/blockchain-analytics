package com.timestamper.blockchain_analytics.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;

public enum MonthSlot {
    DAY_1_TO_4("1일 ~ 4일", 1, 4),
    DAY_5_TO_9("5일 ~ 9일", 5, 9),
    DAY_10_TO_14("10일 ~ 14일", 10, 14),
    DAY_15_TO_19("15일 ~ 19일", 15, 19),
    DAY_20_TO_24("20일 ~ 24일", 20, 24),
    DAY_25_TO_END("25일 ~ 마지막일", 25, 31);

    private final String label;
    private final int startDay;
    private final int endDay;

    MonthSlot(String label, int startDay, int endDay) {
        this.label = label;
        this.startDay = startDay;
        this.endDay = endDay;
    }

    public int getStartDay() {
        if (this == DAY_1_TO_4) {
            return 1;
        }
        return startDay;
    }

    public int getEndDay(YearMonth yearMonth) {
        // 마지막 구간이면 말일 처리
        if (this == DAY_25_TO_END) {
            return yearMonth.lengthOfMonth();
        }
        return endDay;
    }

    public LocalDateTime getStartDateTime(YearMonth ym) {
        return ym.atDay(startDay).atStartOfDay();
    }

    public LocalDateTime getEndDateTime(YearMonth ym) {
        return ym.atDay(getEndDay(ym)).atTime(LocalTime.MAX);
    }

    @JsonValue
    public String getLabel() {
        return label;
    }

    @JsonCreator
    public static MonthSlot fromLabel(String label) {
        for (MonthSlot slot : values()) {
            if (slot.label.equals(label)) {
                return slot;
            }
        }
        throw new IllegalArgumentException("Invalid MonthSlot label: " + label);
    }
}
