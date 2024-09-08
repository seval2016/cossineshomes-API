package com.project.entity.concretes.business;

import ch.qos.logback.core.joran.spi.HostClassAndPropertyDouble;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.beans.PropertyChangeEvent;

@Entity
@Table(name = "category_property_values")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class CategoryPropertyValue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, length = 100)
    private String value;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "category_property_key_id", nullable = false)
    private CategoryPropertyKey categoryPropertyKey;

    @ManyToOne
    @JoinColumn(name = "advert_id", nullable = false)
    private Advert advert;


}

