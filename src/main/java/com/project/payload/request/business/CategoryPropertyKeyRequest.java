package com.project.payload.request.business;

import lombok.Data;
import lombok.*;
import javax.validation.constraints.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class CategoryPropertyKeyRequest {
    @NotBlank(message = "Name cannot be blank")
    @Size(max = 80, message = "Name length must be up to 80 characters")
    private String name;

    @NotBlank(message = "Type cannot be blank")
    private String type;

    @NotNull(message = "BuiltIn cannot be null")
    private Boolean builtIn;

    @NotNull(message = "Category ID cannot be null")
    private Long categoryId;
}
