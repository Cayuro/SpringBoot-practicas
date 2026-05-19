package com.riwi.libros.dto.request;

import jakarta.validation.constraints.Size;

public record LibroUpdateDTO(

        @Size(max = 150)
        String titulo,

        @Size(max = 100)
        String autor,

        @Size(max = 20)
        String isbn,

        int anioPublicacion

) {}