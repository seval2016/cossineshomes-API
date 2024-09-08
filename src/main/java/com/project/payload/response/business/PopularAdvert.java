package com.project.payload.response.business;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class PopularAdvert {
    private int id;
    private String title;
    private String imageUrl;
    private int troa; // Total Requests of Advert
    private int tvoa; // Total Views of Advert
    private int popularityPoint; // PP = 3*TROA + TVOA
}
