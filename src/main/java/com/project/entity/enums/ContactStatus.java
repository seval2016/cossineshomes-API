package com.project.entity.enums;

public enum ContactStatus {

    NOT_OPENED(0, "It is not opened by admins yet"),
    OPENED(1, "It was opened and read");

    private final int statusValue;
    private final String statusDescription;

    ContactStatus(int statusValue, String statusDescription) {
        this.statusValue = statusValue;
        this.statusDescription = statusDescription;
    }

    public int getStatusValue() {
        return statusValue;
    }

    public String getStatusDescription() {
        return statusDescription;
    }

    public static ContactStatus fromValue(int statusValue) {
        for (ContactStatus status : ContactStatus.values()) {
            if (status.getStatusValue() == statusValue) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid status value: " + statusValue);
    }
}
