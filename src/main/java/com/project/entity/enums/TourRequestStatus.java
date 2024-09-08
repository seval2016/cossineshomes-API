package com.project.entity.enums;

public enum TourRequestStatus {

    PENDING(0, "Initial value"),
    APPROVED(1, "Approved Can be approved by owner of property"),
    DECLINED(2, "Declined Can be declined by owner of property"),
    CANCELED(3, "Canceled Can be canceled by owner of tour request");

    private final int tourStatusValue;
    private final String tourDesc;

    TourRequestStatus(int tourStatusValue, String tourDesc) {
        this.tourStatusValue = tourStatusValue;
        this.tourDesc = tourDesc;
    }

    public int getTourStatusValue() {
        return tourStatusValue;
    }

    public String getTourDescription() {
        return tourDesc;
    }

    public static TourRequestStatus fromValue(int tourStatusValue) {
        for (TourRequestStatus tourStatus : TourRequestStatus.values()) {
            if (tourStatus.getTourStatusValue() == tourStatusValue) {
                return tourStatus;
            }
        }
        throw new IllegalArgumentException("Invalid status value: " + tourStatusValue);
    }
}
