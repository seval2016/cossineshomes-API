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

    @ManyToOne
    @JoinColumn(name = "advert_type_id", nullable = false)
    private AdvertType advertType;

    @ManyToOne
    @JoinColumn(name = "country_id", nullable = false)
    private Country country;

    @ManyToOne
    @JoinColumn(name = "city_id", nullable = false)
    private City city;

    @ManyToOne
    @JoinColumn(name = "district_id", nullable = false)
    private District district;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

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
}
