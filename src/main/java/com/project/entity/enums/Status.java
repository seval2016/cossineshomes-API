package com.project.entity.enums;

public enum Status {

    PENDING(0),
    ACTIVATED(1),
    REJECTED(2);

    private final int value;

    Status(int value) {
        this.value = value;

    }

    public int getValue() {
        return value;
    }

    public static Status fromValue(int value) {
        for (Status status : values()) {
            if (status.getValue() == value) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid status value: " + value);
    }
}