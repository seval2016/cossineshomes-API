package com.project.entity.concretes.business;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "images")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Images {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    @Column(nullable = false)
    private byte[] data;

    @Column(nullable = false)
    private String name;

    private String type;

    @Column(nullable = false)
    private Boolean featured;

    @ManyToOne
    @JoinColumn(name = "advert_id", nullable = false)
    private Advert advert;

    public String getUrl() {
        // Assuming you store images somewhere and have a way to retrieve them via a URL
        return "/images/" + this.name; // Adjust this to fit your logic
    }
    // Getters and Setters
    public Boolean getFeatured() {
        return featured;
    }

}
