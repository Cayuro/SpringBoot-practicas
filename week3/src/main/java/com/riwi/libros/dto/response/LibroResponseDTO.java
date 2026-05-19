package com.riwi.libros.dto.response;

public record LibroResponseDTO(

        Long id,
        String titulo,
        String autor,
        String isbn,
        int anioPublicacion

) {}