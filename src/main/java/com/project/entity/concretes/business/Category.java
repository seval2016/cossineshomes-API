package com.project.entity.concretes.business;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "categories")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(nullable = false, length = 50)
    private String icon;

    @Column(nullable = false)
    private boolean builtIn = false;

    @Column(nullable = false)
    private int seq = 0;

    @Column(nullable = false, length = 200)
    private String slug;

    @Column(nullable = false)
    private boolean isActive = true;

    @Column(nullable = false)
    private LocalDateTime createAt;

    @Column
    private LocalDateTime updateAt;

    @OneToMany(mappedBy = "category")
    private List<CategoryPropertyKey> categoryPropertyKeys;

}
