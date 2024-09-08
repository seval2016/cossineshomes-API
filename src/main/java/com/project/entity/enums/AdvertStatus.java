package com.project.entity.enums;

public enum AdvertStatus {

    PENDING(0, "Pending"),
    ACTIVATED(1, "Activated"),
    REJECTED(2, "Rejected");

    private final int value;
    private final String description;

    AdvertStatus(int value, String description) {
        this.value = value;
        this.description = description;
    }

    public int getValue() {
        return value;
    }
    public String getDescription() {
        return description;
    }

    public static AdvertStatus fromValue(int value) {
        for (AdvertStatus status : values()) {
            if (status.getValue() == value) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid status value: " + value);
    }
}