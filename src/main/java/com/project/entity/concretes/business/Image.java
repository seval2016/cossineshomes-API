package com.project.entity.concretes.business;

import com.project.entity.concretes.business.Advert;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Base64;

@Entity
@Table(name = "images")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    private byte[] data;

    private String name;

    private String type;

    private Boolean featured=false;

    @ManyToOne
    @JoinColumn(name = "advert_id")
    private Advert advert;


    public String getUrl() {
        // Burada URL oluşturma mantığını ekleyin
        // Örneğin, bir Base64 string ile dönebilir veya bir dosya yolunu verebilirsiniz
        return "data:" + type + ";base64," + Base64.getEncoder().encodeToString(data);
    }
}