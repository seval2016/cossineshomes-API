package com.project.payload.response.business.category;

import com.project.entity.concretes.business.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class CategoryAdvertResponse {

    private String category; //İlanın bağlı olduğu kategori adını tutuyor.
    private int amount; //Bu kategoriye ait ilanların sayısını tutuyor.
}
