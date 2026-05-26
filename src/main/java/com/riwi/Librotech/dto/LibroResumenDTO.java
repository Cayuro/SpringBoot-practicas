package com.riwi.Librotech.dto;

import java.time.LocalDate;

// laboratorio semana 4 #2
public record LibroResumenDTO (
    Long id,
    String titulo,
    LocalDate fechaPublicacion,
    Double precio,
    String editorialNombre,
    String pais
){}
