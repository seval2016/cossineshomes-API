package com.project.payload.request.business;

import com.project.entity.concretes.business.Category;
import com.project.entity.concretes.business.CategoryPropertyValue;
import com.project.entity.enums.CategoryPropertyKeyType;
import lombok.Data;
import lombok.*;
import javax.validation.constraints.*;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class CategoryPropertyKeyRequest {


    @NotBlank(message = "Name cannot be blank")
    @Size(min = 2, max = 80, message = "Name must be between 2 and 80 characters")
    private String name;

    @NotBlank(message = "Type cannot be blank")
    @Pattern(regexp = "string|number|boolean", message = "Type must be one of: string, number, boolean")
    private String type;

    private Long categoryId;

    private Boolean builtIn;

}
