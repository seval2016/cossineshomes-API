package com.project.payload.response.business;

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

    //Bu DTO sınıfı, kategori adı ve bu kategorideki reklamların sayısını temsil eder.
    private String category;
    private long amount;
}
