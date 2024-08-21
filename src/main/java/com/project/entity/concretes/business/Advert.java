package com.project.entity.concretes.business;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.project.entity.concretes.user.User;
import lombok.*;


import javax.persistence.*;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "adverts")

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Advert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    @Size(min = 5, max = 150)
    private String title;

    @Column(length = 300)
    private String description;

    @Column(nullable = false, length = 200, unique = true)
    @Size(min = 5, max = 200)
    private String slug;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private int status = 0;

    @Column(nullable = false)
    private boolean builtIn = false;

    @Column(nullable = false)
    private boolean isActive = true;

    @Column(nullable = false)
    private int viewCount = 0;

    @Column(length = 255)
    private String location;

    /*@ManyToOne
    @JoinColumn(name = "advert_type_id", nullable = false)
    private AdvertType advertType;

    @ManyToOne
    @JoinColumn(name = "country_id",nullable = false)
    private Country country;

    @ManyToOne
    @JoinColumn(name = "city_id",nullable = false)
    private City city;

    @ManyToOne
    @JoinColumn(name = "district_id",nullable = false)
    private District district;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;*/

    @Column(nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd")
    private LocalDateTime createAt;

    @Column
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd")
    private LocalDateTime updateAt;

    // Getter, Setter, Constructors
}
