package com.project.entity.concretes.business;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.project.entity.enums.AdvertStatus;
import com.project.entity.concretes.user.User;
import com.project.service.helper.SlugUtils;
import lombok.*;


import javax.persistence.*;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.List;

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

    @Column(nullable = false)
    @Size(min = 5, max = 150)
    private String title;

    @Column(length = 300)
    private String description;

    @Column(nullable = false, length = 200)
    @Size(min = 5, max = 200)
    private String slug;

    @Column(nullable = false)
    private Double price = 0.0;

    @Column(nullable = false)
    private int status = AdvertStatus.PENDING.getValue();

    @Column(nullable = false)
    private boolean builtIn = false;

    @Column(nullable = false)
    private boolean isActive = true;

    @Column(nullable = false)
    private int viewCount = 0;

    @Column(nullable = false)
    private String location;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Turkey")
    @Column(nullable = false)
    private LocalDateTime createAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Turkey")
    private LocalDateTime updateAt;

    @PrePersist
    protected void onCreate() {
        createAt = LocalDateTime.now();
        updateAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updateAt = LocalDateTime.now();
    }

    @PostPersist
    @PostUpdate
    public void generateSlug() {
        if (this.slug == null || this.slug.isEmpty()) {
            this.slug = SlugUtils.toSlug(this.title) + "-" + this.id;
        }
    }


    //------------İlişkili sütunlar -------------

    //ManyToOne
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "advert_type_id", nullable = false)
    private AdvertType advertType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_id", nullable = false)
    private Country country;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "city_id", nullable = false)
    private City city;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "district_id", nullable = false)
    private District district;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name="user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    //----OneToMany

    @OneToMany(mappedBy = "advert", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Images> images;

    @OneToMany(mappedBy = "advert", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<TourRequest> tourRequestList;

    @OneToMany(mappedBy = "advert", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonIgnore
    private List<Favorite> favoritesList;

    @OneToMany(mappedBy = "advert", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<CategoryPropertyValue> categoryPropertyValuesList;

    @OneToMany(mappedBy = "advert", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Images> imagesList = new ArrayList<>();

    // Corrected mapping
    @OneToMany(mappedBy = "advert", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Log> logList;


}
