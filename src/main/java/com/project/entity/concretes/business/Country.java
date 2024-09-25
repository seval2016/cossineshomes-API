package com.project.entity.concretes.business;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "countries")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Country {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length =30)
    private String name;

    @Column(nullable = false, length = 3) // Add this for ISO code
    private String isoCode;

    @OneToMany (mappedBy = "country",cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<City> city= new HashSet<>();

    @OneToMany (mappedBy = "country",cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Advert> advertList = new ArrayList<>();
}
