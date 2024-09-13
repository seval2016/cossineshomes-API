package com.project.entity.concretes.business;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "category_property_keys")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryPropertyKey {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "name can not be null")
    @NotBlank(message = "name can not be white space")
    @Size(min=2, max=80, message = "name '${validatedValue}' must be between {min} and {max} long")
    @Column(nullable = false, length = 80)
    private String name;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private boolean builtIn = false;

    @JsonIgnore // sonsuz döngüye girilmesin diye @JsonIgnore eklendi
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    // mappedBy değerini doğru alan ile güncelledim
    @OneToMany(mappedBy = "categoryPropertyKey", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<CategoryPropertyValue> categoryPropertyValues = new ArrayList<>();
}

