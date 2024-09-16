package com.project.payload.request.business;

import lombok.*;

import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder(toBuilder = true)
public class CreateAdvertPropertyRequest {

    @NotNull(message = "key can not be blank")
    private Long keyId;

    @NotNull(message = "value can not be null")
    private String value;
}
