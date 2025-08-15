package com.timestamper.blockchain_analytics.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.time.LocalDate;
import java.time.LocalDateTime;

public enum DaySlot {
    MIDNIGHT_TO_FOUR("00:00 ~ 03:59", 0),
    FOUR_TO_EIGHT("04:00 ~ 07:59", 4),
    EIGHT_TO_NOON("08:00 ~ 11:59", 8),
    NOON_TO_FOUR("12:00 ~ 15:59", 12),
    FOUR_TO_EIGHT_PM("16:00 ~ 19:59", 16),
    EIGHT_TO_MIDNIGHT("20:00 ~ 23:59", 20);

    private final String label;
    private final int startHour;

    DaySlot(String label, int startHour) {
        this.label = label;
        this.startHour = startHour;
    }

    @JsonValue
    public String getLabel() {
        return label;
    }

    @JsonCreator
    public static DaySlot fromLabel(String label) {
        for (DaySlot slot : values()) {
            if (slot.label.equals(label)) {
                return slot;
            }
        }
        throw new IllegalArgumentException("Invalid TimeSlot label: " + label);
    }

    public LocalDateTime getStartDateTime(LocalDate date) {
        return date.atTime(startHour, 0);
    }

    public LocalDateTime getEndDateTime(LocalDate date) {
        return getStartDateTime(date).plusHours(4).minusNanos(1);
    }
}
