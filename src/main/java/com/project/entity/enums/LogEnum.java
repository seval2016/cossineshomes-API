package com.project.entity.enums;

public enum LogEnum {

    CREATED("Advert is created and waiting for approval"),
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
            // Büyük/küçük harf duyarlılığını kaldırmak için equalsIgnoreCase kullanılabilir
            if (logDescription.getDescription().equalsIgnoreCase(description)) {
                return logDescription;
            }
        }
        // Hata mesajını özelleştirme
        throw new IllegalArgumentException("Invalid log description: '" + description + "'. Available descriptions: "
                + getAvailableDescriptions());
    }

    // Tüm açıklamaları string formatında döndüren yardımcı bir metot
    private static String getAvailableDescriptions() {
        StringBuilder descriptions = new StringBuilder();
        for (LogEnum logEnum : LogEnum.values()) {
            descriptions.append(logEnum.getDescription()).append(", ");
        }
        // Son virgülü ve boşluğu kaldırma
        if (descriptions.length() > 0) {
            descriptions.setLength(descriptions.length() - 2);
        }
        return descriptions.toString();
    }
}
