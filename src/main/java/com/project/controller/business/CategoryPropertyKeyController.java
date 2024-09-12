package com.project.controller.business;

import com.project.service.business.CategoryPropertyKeyService;
import com.project.service.business.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/category_property_keys")
@RequiredArgsConstructor
public class CategoryPropertyKeyController {

    private CategoryService categoryService;
    private CategoryPropertyKeyService categoryPropertyKeyService;
}
