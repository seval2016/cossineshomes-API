package com.project.payload.response.business.image;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class ImageResponse {

    private Long id;
    private String name;
    private String type;
    private Boolean featured;
    private String url;

    public ImageResponse(Long id,String name,String type,Boolean featured){
        this.id=id;
        this.name=name;
        this.type=type;
        this.featured=featured;

    }
    public ImageResponse(Long id,String url){
        this.id=id;
        this.url=url;

    }
}
