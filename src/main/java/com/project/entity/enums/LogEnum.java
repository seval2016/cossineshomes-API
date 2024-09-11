package com.project.entity.enums;

public enum LogEnum {

    CREATED("Advert is created and wait for approve"),
    UPDATED("Advert is updated"),
    DELETED("Advert is deleted"),
    DECLINED("Advert is declined by manager"),
    TOUR_REQUEST_CREATED("Tour request is created"),
    TOUR_REQUEST_ACCEPTED("Tour request is accepted"),
    TOUR_REQUEST_DECLINED("Tour request is declined"),
    TOUR_REQUEST_CANCELED("Tour request is canceled");

    private final String description;

    LogEnum(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static LogEnum fromDescription(String description) {
        for (LogEnum logDescription : LogEnum.values()) {
            if (logDescription.getDescription().equals(description)) {
                return logDescription;
            }
        }
        throw new IllegalArgumentException("Invalid log description: " + description);
    }
}
