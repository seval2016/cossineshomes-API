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
    private boolean featured;
    private Long advertId;
    private String url;
    private byte[] data;

    public ImageResponse(Long id,String name,String type,Boolean featured,Long advertId,
                         byte[] data){
        this.id=id;
        this.name=name;
        this.type=type;
        this.featured=featured;
        this.advertId=advertId;
        this.data=data;

    }
    public ImageResponse(Long id,String name,String type,Boolean featured){
        this.id=id;
        this.name=name;
        this.type=type;
        this.featured=featured;

    }
    public ImageResponse(Long id,byte[] data){
        this.id=id;
        this.data=data;

    }
}
