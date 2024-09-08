package com.project.service.helper;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Objects;

@Component
public class PageableHelper {

    public Pageable getPageableWithProperties(int page, int size, String sort, String type){
        Pageable pageable = PageRequest.of(page,size, Sort.by(sort).ascending());
        if(Objects.equals(type, "desc")){
            pageable = PageRequest.of(page,size, Sort.by(sort).descending());
        }
        return pageable;
    }


    public Pageable getPageableWithProperties(int page, int size){
        return PageRequest.of(page,size,Sort.by("id").descending());
    }

    public Pageable getPageableWithProperties(String q, Long categoryId, Long advertTypeId,
                                              BigDecimal priceStart, BigDecimal priceEnd,
                                              Integer status, int page, int size,
                                              String sort, String type) {

        // Varsayılan olarak ascending (artan) sıralama yapılıyor
        Sort.Direction direction = Sort.Direction.ASC;

        // Sıralama tipi "desc" ise, direction'ı değiştirelim
        if (Objects.equals(type, "desc")) {
            direction = Sort.Direction.DESC;
        }

        // Sıralama için geçerli olan alanı kontrol
        Sort sortOrder = Sort.by(direction, sort != null ? sort : "id");

        // Sayfalama ve sıralama parametrelerini kullanarak Pageable nesnesini oluşturalım
        Pageable pageable = PageRequest.of(page, size, sortOrder);

        return pageable;
    }
}