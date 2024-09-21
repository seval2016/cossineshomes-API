package com.project.payload.response.business.advert;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AdvertResponseForUser {

    private Long id;
    private Long userId;
    private String title;
    private String description;
    private String slug;
    private BigDecimal price;
    private int status;
    private Boolean builtIn;
    private Boolean isActive;
    private Integer viewCount;
    private String location;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;

    // Özellikler
    private Map<String, Object> properties; // Oda, banyo vb. gibi özellikler

    // İlanın resim listesi
    private List<String> images;

    // Tur talepleri listesi
    private List<String> tourRequests;
}
