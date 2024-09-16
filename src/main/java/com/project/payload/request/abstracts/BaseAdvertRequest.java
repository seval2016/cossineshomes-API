package com.project.payload.request.abstracts;

import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class BaseAdvertRequest extends AbstractAdvertRequest {

    @NotNull(message = "Status must not be empty")
    private int status;


    private Boolean builtIn;

    private Boolean isActive= true;


    private Integer viewCount;
}
