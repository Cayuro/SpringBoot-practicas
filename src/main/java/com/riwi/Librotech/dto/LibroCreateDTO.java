package com.riwi.Librotech.dto;

import java.time.LocalDate;
import java.util.Set;

public record LibroCreateDTO(
    String titulo,
    String autor,
    String isbn,
    LocalDate fechaPublicacion,
    Double precio,
    Long editorialId,    // cliente envia id de editorial
    Set<Long> generoIds // envia solo id de generos
)
 {
    
}
