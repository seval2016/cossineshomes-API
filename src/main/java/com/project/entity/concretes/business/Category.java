package com.project.entity.concretes.business;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "categories")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(length = 50)
    private String icon;

    @Column(nullable = false)
    private boolean builtIn=false;

    @Column(nullable = false)
    private int seq=0;

    @Column(nullable = false)
    @Size(min = 5, max = 200)
    private String slug;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT true")
    private boolean is_active;

    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd")
    @Column(name = "create_at", nullable = false)
    private LocalDateTime createAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd")
    @Column(name = "update_at")
    private LocalDateTime updateAt;

    // Advert listesi ekleniyor
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Advert> adverts= new ArrayList<>();

    @OneToMany(mappedBy = "category" , cascade=CascadeType.ALL, orphanRemoval = true)
    private Set<CategoryPropertyKey> categoryPropertyKeys = new HashSet<>();

    @PrePersist
    protected void onCreate() {
        createAt = LocalDateTime.now();
        updateAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updateAt = LocalDateTime.now();
    }
}

