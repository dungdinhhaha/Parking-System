package com.parking.system.enums;

public enum SubscriptionCycleType {
    WEEKLY(7),
    MONTHLY(30),
    YEARLY(365);

    private final int days;

    SubscriptionCycleType(int days) {
        this.days = days;
    }

    public int getDays() {
        return days;
    }
}
