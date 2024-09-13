package com.project.entity.concretes.business;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "districts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class District {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 30)
    private String name;

    private Boolean builtIn;

    @ManyToOne
    @JoinColumn(name = "city_id", nullable = false)
    private City city;
}
