package com.project.entity.enums;

public enum TourRequestEnum {

    PENDING(0, "Initial value"),
    APPROVED(1, "Approved Can be approved by owner of property"),
    DECLINED(2, "Declined Can be declined by owner of property"),
    CANCELED(3, "Canceled Can be canceled by owner of tour request");

    private final int value;
    private final String tourDesc;

    TourRequestEnum(int value, String tourDesc) {
        this.value = value;
        this.tourDesc = tourDesc;
    }

    public int getValue() {
        return value;
    }

    public String getTourDescription() {
        return tourDesc;
    }

    public static TourRequestEnum fromValue(int value) {
        for (TourRequestEnum tourStatus : TourRequestEnum.values()) {
            if (tourStatus.getValue() == value) {
                return tourStatus;
            }
        }
        throw new IllegalArgumentException("Invalid status value: " + value);
    }
}
