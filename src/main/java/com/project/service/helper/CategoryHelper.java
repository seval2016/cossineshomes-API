package com.project.service.helper;

import com.project.entity.concretes.business.Category;
import com.project.entity.concretes.business.CategoryPropertyKey;
import com.project.entity.enums.CategoryPropertyKeyType;
import com.project.exception.BadRequestException;
import com.project.exception.ResourceNotFoundException;
import com.project.payload.messages.ErrorMessages;
import com.project.payload.request.business.JsonCategoryPropertyKeyRequest;
import com.project.repository.business.CategoryPropertyKeyRepository;
import com.project.repository.business.CategoryRepository;
import com.project.service.business.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CategoryHelper {

    private final CategoryRepository categoryRepository;
    private final CategoryPropertyKeyRepository categoryPropertyKeyRepository;


    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id).orElseThrow(() -> new RuntimeException(ErrorMessages.CATEGORY_NOT_FOUND));
    }

    public List<Category> getCategoryByTitle(String category) {

        return categoryRepository.findByTitle(category).orElseThrow(
                () -> new BadRequestException(ErrorMessages.CATEGORY_NOT_FOUND)
        );
    }

    public void generateCategory() {
        if (categoryRepository.findAll().isEmpty()) {
            List<Category> categories = List.of(
                    Category.builder()
                            .id(1L)
                            .title("Müstakil Ev")
                            .icon("ev_icon")
                            .builtIn(true)
                            .seq(0)
                            .slug("mustakil-ev")
                            .isActive(true)
                            .build(),
                    Category.builder()
                            .id(2L)
                            .title("Apartman Dairesi")
                            .icon("dairesi_icon")
                            .builtIn(true)
                            .seq(0)
                            .slug("apartman-dairesi")
                            .isActive(true)
                            .build(),
                    Category.builder()
                            .id(3L)
                            .title("Ofis")
                            .icon("ofis_icon")
                            .builtIn(true)
                            .seq(0)
                            .slug("kelepir-ofis")
                            .isActive(true)
                            .build(),
                    Category.builder()
                            .id(4L)
                            .title("Villa")
                            .icon("villa_icon")
                            .builtIn(true)
                            .seq(0)
                            .slug("kelepir-villa")
                            .isActive(true)
                            .build(),
                    Category.builder()
                            .id(5L)
                            .title("Arsa")
                            .icon("arsa_icon")
                            .builtIn(true)
                            .seq(0)
                            .slug("kelepir-arsa")
                            .isActive(true)
                            .build()
            );

            // Kategorileri kaydet
            categoryRepository.saveAll(categories);
        }
    }

    public List<Category> getAllCategory() {
        return categoryRepository.findAll();
    }

    public Category findCategoryById(Long id) {
        return categoryRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(ErrorMessages.CATEGORY_NOT_FOUND + id));
    }

    public void generateCategoryPropertyKeys() {
        if (categoryPropertyKeyRepository.findAll().isEmpty()) {

            String[] housePropertyName = {"Number of Rooms and Living Rooms", "Number of Bathrooms", "Building Age", "Gross Square Meters", "Garden", "Garage", "Min. Price", "Max. Price"};
            String[] apartmentPropertyName = {"Number of Rooms and Living Rooms", "Number of Bathrooms", "Building Age", "Gross Square Meters", "Balcony", "Garage", "Min. Price", "Max. Price"};
            String[] officePropertyName = {"Number of Rooms and Living Rooms", "Number of Bathrooms", "Building Age", "Gross Square Meters", "Storage", "Garage", "Min. Price", "Max. Price"};
            String[] villaPropertyName = {"Number of Rooms and Living Rooms", "Number of Bathrooms", "Building Age", "Gross Square Meters", "Storage", "Garage", "Min. Price", "Max. Price"};
            String[] landPropertyName = {"Square Meters", "Min. Price", "Max. Price"};

            CategoryPropertyKeyType[] propertyTypes1 = {CategoryPropertyKeyType.NUMBER, CategoryPropertyKeyType.NUMBER, CategoryPropertyKeyType.NUMBER, CategoryPropertyKeyType.NUMBER, CategoryPropertyKeyType.BOOLEAN, CategoryPropertyKeyType.BOOLEAN, CategoryPropertyKeyType.DOUBLE, CategoryPropertyKeyType.DOUBLE};
            CategoryPropertyKeyType[] propertyTypes2 = {CategoryPropertyKeyType.NUMBER, CategoryPropertyKeyType.DOUBLE, CategoryPropertyKeyType.DOUBLE};

            JsonCategoryPropertyKeyRequest[] arr = new JsonCategoryPropertyKeyRequest[5];
            arr[0] = new JsonCategoryPropertyKeyRequest(1L, housePropertyName, true);
            arr[1] = new JsonCategoryPropertyKeyRequest(2L, apartmentPropertyName, true);
            arr[2] = new JsonCategoryPropertyKeyRequest(3L, officePropertyName, true);
            arr[3] = new JsonCategoryPropertyKeyRequest(4L, villaPropertyName, true);
            arr[4] = new JsonCategoryPropertyKeyRequest(5L, landPropertyName, true);

            for (JsonCategoryPropertyKeyRequest request : arr) {

                String[] propertyName = {};
                CategoryPropertyKeyType[] propertyTypes = {};


                switch (request.getId().intValue()) {
                    case 1:
                        propertyName = housePropertyName;
                        propertyTypes = propertyTypes1;
                        break;
                    case 2:
                        propertyName = apartmentPropertyName;
                        propertyTypes = propertyTypes1;
                        break;
                    case 3:
                        propertyName = officePropertyName;
                        propertyTypes = propertyTypes1;
                        break;
                    case 4:
                        propertyName = villaPropertyName;
                        propertyTypes = propertyTypes1;
                        break;
                    case 5:
                        propertyName = landPropertyName;
                        propertyTypes = propertyTypes2;
                        break;
                    default:
                        throw new IllegalArgumentException("Invalid category ID");
                }

                Category category = getCategoryById(request.getId());

                for (int i = 0; i < propertyName.length; i++) {
                    CategoryPropertyKey props = CategoryPropertyKey.builder()
                            .name(propertyName[i])
                            .builtIn(request.getBuiltIn())
                            .type(propertyTypes[i])
                            .category(category)
                            .build();
                    categoryPropertyKeyRepository.save(props);

                }

            }

        }
    }
}
