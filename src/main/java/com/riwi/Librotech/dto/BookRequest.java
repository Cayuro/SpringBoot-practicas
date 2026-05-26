package com.riwi.Librotech.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

import com.riwi.Librotech.validation.OnCreate;
import com.riwi.Librotech.validation.OnUpdate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookRequest {

    @NotBlank(message = "Title field is required", groups = {OnCreate.class , OnUpdate.class})
    private String titulo;

    @NotBlank(message = "Author field is required", groups = {OnCreate.class , OnUpdate.class})
    private String autor;

    @NotBlank(message = "isbn field is required", groups = {OnCreate.class , OnUpdate.class})
    private String isbn;

    private LocalDate fechaPublicacion;
}
