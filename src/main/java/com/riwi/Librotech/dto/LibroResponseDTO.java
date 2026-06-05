package com.riwi.Librotech.dto;

import java.time.LocalDate;
import java.util.Set;

public record LibroResponseDTO (
    Long id,
    String titulo,
    String autor,
    String isbn,
    LocalDate fechaPublicacion,
    Double precio,
    String nombreEditorial,
    Set<String> generos
)
{
}
