package com.project.payload.request.business;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class AdvertTypeRequest {

    @NotNull(message = "Please enter title")
    @Size(min = 2 , max = 30 , message = "Title must have min 2 chars and max 30 chars")
    @Pattern(regexp = "\\A(?!\\s*\\Z).+" ,message="Title must consist of the characters .")
    private String title;
}
