package com.project.payload.request.business;

import lombok.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.File;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class ImagesRequest {

    @NotNull
    private Long advertId;

    @NotEmpty
    private List<File> Images;
}
