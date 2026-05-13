package com.riwi.libros.dto.response;

public record LibroRecomendadoDTO(

        Long id,
        String titulo,
        String autor,
        String isbn,
        int anioPublicacion,
        double score,
        String razon

) {}
