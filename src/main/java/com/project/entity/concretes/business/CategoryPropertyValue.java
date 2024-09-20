package com.project.entity.concretes.business;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "category_property_values")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class CategoryPropertyValue {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String value;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "advert_id", nullable = false)
    private Advert advert;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_property_key_id", nullable = false)
    private CategoryPropertyKey categoryPropertyKey;

    }

