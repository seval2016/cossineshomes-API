package com.project.controller.business;

import com.project.service.business.AdvertService;
import com.project.service.business.CategoryPropertyKeyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/category_property_values")
@RequiredArgsConstructor
public class CategoryPropertyValueController {
    private AdvertService advertService;
    private CategoryPropertyKeyService categoryPropertyKeyService;
    private AdvertService AdvertService;
}
