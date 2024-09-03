package com.project.payload.request.business;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.project.entity.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class AdvertRequest {


    @NotNull(message = " Title must not be empty")
    @Size(min = 5, max = 150)
    private String title;

    @NotNull(message = " Description must not be empty")
    @Size(max = 300)
    private String description;

    @NotNull ( message = " Slug must not be empty")
    @Size(min = 5, max = 200)
    private String slug;

    @NotNull(message = " Price  must not be empty")
    private BigDecimal price;

    @NotNull(message = " Status must not be empty")
    private Status status = Status.PENDING;

    // @NotNull
    // private Boolean builtIn =false;

    @NotNull(message = " View Count must not be empty")
    private int viewCount = 0;

    // @NotNull
    // private Boolean isActive = true;

    private String location;

    @NotNull(message = " Create date must not be empty")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate createdAt;

    @Column(nullable = true)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate updatedAt;
}