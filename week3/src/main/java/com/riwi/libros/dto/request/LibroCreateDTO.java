package com.riwi.libros.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LibroCreateDTO(

        @NotBlank
        @Size(max = 150)
        String titulo,

        @NotBlank
        @Size(max = 100)
        String autor,

        @Size(max = 20)
        String isbn,

        int anioPublicacion

) {}