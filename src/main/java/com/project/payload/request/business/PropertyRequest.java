package com.project.payload.request.business;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class PropertyRequest {

    @NotNull(message = "key can not be blank")
    private Long keyId;

    @NotNull(message = "value can not be null")
    private String value;
}
