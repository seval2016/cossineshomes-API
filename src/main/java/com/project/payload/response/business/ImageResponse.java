package com.project.payload.response.business;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class ImageResponse {

    private Long id;
    private String name;
    private String type;
    private boolean featured;
    private Long advertId;
    private String url;
    private byte[] data;
}
